import cv2
import numpy as np
import matplotlib.pyplot as plt

def normalize(a, n, m):
  s = 0
  for i in range(n):
    for j in range(m):
      if a[i][j] > s:
        s = a[i][j]
  for i in range(n):
    for j in range(m):
      a[i][j] = a[i][j]*255/s
  return a

def angleFft(a, n, m):
    m1 = m2 = 0
    i1, j1, i2, j2 = 0, 0, 0, 0
    for i in range(n):
      for j in range(m):
        x = a[i][j]
        if x > m2:
          if x >= m1:
            m1, m2 = x, m1   
            i1, j1, i2, j2 = i, j, i1, j1
          else:
            m2 = x
            i2, j2 = i, j
    print( i1,j1, i2,j2)
    if i1 == i2:
      return np.pi/2
    return np.arctan((j2-j1)/(i2-i1))*180/np.pi
    

def findAngleWithFft(img, n, m):
  fft_complex = np.fft.fft2((255-img))
  fft_modules = normalize(np.array([[np.abs(i)**2 for i in I] for I in fft_complex]), n, m)
  return angleFft(fft_modules, n, m)*180/np.pi
  

def findAngleWithHoughLines(img, n, m):
  img = cv2.Canny(img,50, 200, 3)
  lines = cv2.HoughLines(img,0.5,np.pi/720,int(m/5))
  thetas = []
  for line in lines:
    theta = line[0][1]
#    print(theta)
    thetas.append(theta-np.pi/2)
#  return (np.sum(thetas)*180/np.pi)/len(thetas)
  return np.median(thetas)*180/np.pi 



def rotateImage(img, n, m, angle):
  center = ((m -1)/2.0, (n-1)/2.0)
  rot = cv2.getRotationMatrix2D(center, angle, 1.0)
  cos = np.abs(rot[0, 0])
  sin = np.abs(rot[0, 1])
  nm = int((n * sin) + (m * cos))
  nn = int((n * cos) + (m * sin))
  rot[0, 2] += (nm / 2) - center[0]
  rot[1, 2] += (nn / 2) - center[1]
  result = cv2.warpAffine(img, rot, (nm, nn) , flags=cv2.INTER_LINEAR)
  return result
  
def projection(img, angle):
#  center = np.array([(m -1)/2.0, (n-1)/2.0])
  n, m = img.shape[:2]
  projVector = np.array([np.cos(angle), np.sin(angle)])
  corners = [(0,0), (n-1,0), (n-1,m-1), (0,m-1)]
  majorPoints = sorted([np.dot(corner, projVector) for corner in corners])
  Max, Min = majorPoints[3], majorPoints[0]
  print(Max, Min)
#  projVector /= (Max - Min)
#  C = Min / (Max - Min)
  projections = [0]*int(Max - Min+1)
  for i in range(n):
    for j in range(m):
      try:
        projections[int(np.dot(np.array([i,j]), projVector)-Min)] += 1-img[i][j]/255.0
      except IndexError:
        print(Max - Min, np.dot(np.array([i,j]), projVector)-Min, n, i, m, j)
#  plt.plot(range(int(Max-Min+1)), projections)
  return projections


def findAngleWithProjection(img, n, m):
  p, maxval = 0, 0
  for i in range(360):
    val = max(projection(img, i*np.pi/360))
    if val>maxval:
      maxval = val
      p = i
  return np.pi*p/2

def findAngleWithProjectionOptimized(img, n, m):
  
  p1, maxval = 0, 0
  for i in range(36):
    val = max(projection(img, i*np.pi/36))
    if val>maxval:
      maxval = val
      p1 = i
      
  p2 = p1 * 5
  for i in range(-9, 10):
    val = max(projection(img, p1*np.pi/36 + i*np.pi/180))
    if val>maxval:
      maxval = val
      p2 = p1 * 5 + i
  
  p3 = p2 * 4
  for i in range(-2,3):
    val = max(projection(img, p2*np.pi/180 + i*np.pi/720))
    if val>maxval:
      maxval = val
      p3 = p2 * 4 + i
   
  return np.pi*p3/4

def getBoundingBoxes(img, n, m):
  draw = raw
  canny = raw
  canny = cv2.Canny(raw,50, 200, 3) 
#  cv2.imshow('canny', canny)  
  im2, contours, hierarchy = cv2.findContours(canny, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
  cv2.drawContours(draw, contours, 1, (120,255,0), 3)
  cv2.imshow("contours",draw)
  rects = []
  for i in range(len(contours)):
    polyDP = cv2.approxPolyDP(contours[i], 1, True)
    boundingRectangle = cv2.boundingRect(polyDP)
    rects.append(boundingRectangle)
  for b in rects:
    if b[2] > m / 20:
      cv2.rectangle(draw, (b[0],b[1]), (b[0]+b[2],b[1]+b[3]), 120)
  return draw, rects


def isMax(array, i):
  if i > 0:
    if array[i]<array[i-1]:
      return False
  if i < len(array)-1:
    if array[i]<array[i+1]:
      return False
  return True

def localMaxes(array):
  maxs = []
  for i in range(len(array)):
    if isMax(array, i):
      maxs.append(i)
  return maxs  
  
  
#def getSingleStaves(img, n, m):
#  withBoxes, rects = getBoundingBoxes(255-raw, n, m)
##  projValues = projection(img, 0)
#  indexes = [0]
#  goodRects = []
#  print (m, n)
#  for i in range(len(rects)-1):
#    print (rects[i])
##    print (rects[i][1],rects[i][3], m/3:
#    if rects[i][2] > m / 3 and rects[i][3]<n-1:  
#      print ("Yay")
#      goodRects.append(rects[i])
#      
#  goodRects.sort(key = lambda i: i[1])
#  for i in range(len(goodRects)-1):
##    print (rects[i])
##    print (rects[i][1],rects[i][3], m/3:
#    indexes.append((goodRects[i][1]+goodRects[i][3] + goodRects[i+1][1])//2)
#  indexes.append(n-1)
#  staves = []
#  for i in range(len(indexes) - 1):
#    ""
#    staves.append(img[indexes[i]:indexes[i+1], 0:m-1])
#    print (indexes[i+1] - indexes[i])
#    print (staves[i-1].shape)
#  return staves
  

#def find_skeleton3(img):
#    skeleton = np.zeros(img.shape,np.uint8)
#    eroded = np.zeros(img.shape,np.uint8)
#    temp = np.zeros(img.shape,np.uint8)
#
#    _,thresh = cv2.threshold(img,127,255,0)
#
#    kernel = cv2.getStructuringElement(cv2.MORPH_CROSS,(3,3))
#
#    iters = 0
#    while(True):
#        cv2.erode(thresh, kernel, eroded)
#        cv2.dilate(eroded, kernel, temp)
#        cv2.subtract(thresh, temp, temp)
#        cv2.bitwise_or(skeleton, temp, skeleton)
#        thresh, eroded = eroded, thresh # Swap instead of copy
#
#        iters += 1
#        if cv2.countNonZero(thresh) == 0:
#            return skeleton


#def getSingleStaves(img, n, m):
#  
#  projections = projection(raw, 0)
#  minValue = min(projections)
#  maxValue= max(projections)
#  maxs = localMaxes(projections)
#  tol1 = minValue + (maxValue - minValue) / 100
#  tol2 = minValue + (maxValue - minValue) / 100
#  spaces, current= [], 0
#  for i in range(len(projections)-1):
#    if (projections[i] <= tol1 and projections[i+1] > tol2):
#      spaces.append([current, i])
#      current = i + 1
#    elif (projections[i] > tol2 and projections[i+1] <= tol1):
#      current = i
#  spaces.append([current, i+1])
#  spaceLines = []
#  for s in spaces:
#    spaceLines.append((s[0] + s[1]) // 2)
#    print(s)
##  distances = [spaceLines[s+1] - spaceLines[s] for s in range(len(spaceLines)-1)]
##  mmm = max(distances)
##  index = 0
##  spaceLinesFinal = []
##  for d in range(len(distances)):
##    if distances[d] > 0.8 * mmm:
##      spaceLinesFinal.append()
#  staves = []
#  for s in range(len(spaces)-1):
#    print(spaces[s])
#    staves.append(img[spaceLines[s]:spaceLines[s+1], 0:m-1]) 
#  return staves
            
def getSingleStaves(img, n, m):
  
  #Hough lines for whole image
  canny = cv2.Canny(img,50, 200, 3)
  lines = cv2.HoughLines(canny,0.5 ,np.pi/360,int(m/5))
  goodLines = []
  
  #taking horizontal lines
  for line in lines:
    if np.abs(line[0][1]-np.pi/2)<0.01:
      goodLines.append(line[0][0])
  
  #sorting them by y-coordinate and finding max distance between 
  #2 lines = distance between 2 staves
  goodLines.sort()
  maxDistance = max(goodLines[i+1] - goodLines[i] for i in range(len(goodLines)-1))
  print(goodLines, maxDistance)
  
  #getting separation lines
  sepLines = [int(goodLines[0]//2)]
  for i in range(len(goodLines)-1):
    if goodLines[i+1] - goodLines[i] > 0.9 * maxDistance and goodLines[i+1] - goodLines[i] > goodLines[0] / 2:
      sepLines.append((goodLines[i]) + (goodLines[i+1] - goodLines[i])//2)
  sepLines.append(int(goodLines[-1]+n-1)//2)
  
  #getting subimages and line y-coordinates for single staves
  staves = []
  staffLines = []
  for s in range(len(sepLines)-1):
    if sepLines[s] + 6 < sepLines[s+1]:
      staves.append(img[int(sepLines[s]):int(sepLines[s+1]), 0:m-1]) 
      staffLine = [line for line in goodLines if line <=  sepLines[s+1] and line >= sepLines[s]]
      topLine, bottomLine = min(staffLine), max(staffLine)
      staffLine = [int(topLine + i * (bottomLine - topLine) / 4 - sepLines[s]) for i in range(5)]
      staffLines.append(staffLine)
    
  return staves, staffLines
      
def drawStaffLines(staves, staffLines, i):
  
  staff = staves[i]
  staffLine = staffLines[i]
  for y in staffLine:
    pt1 = (1000, int(y))
    pt2 = (-1000, int(y))
    cv2.line(staff, pt1, pt2, 120, 1, cv2.LINE_AA)
     
  return staff

def regions(staves, staffLines, j):
  
  staff = staves[j]
  staffLine = staffLines[j]
  
  projections = projection(staff, np.pi/2)
  
  plt.plot(range(len(projections)), projections)
  plt.plot(range(len(projections)), [sum(projections)/len(projections)]*len(projections))
  
  average = sum(projections)/len(projections)
  regions, current= [], 0
  for i in range(len(projections)-1):
    if (projections[i] <= average and projections[i+1] > average):
      regions.append([current, i])
      current = i + 1
    elif (projections[i] > average and projections[i+1] <= average):
      current = i
  
  roi = [0]
  for i in range(len(regions)):
    roi.append((regions[i][1]+regions[i][0])//2)
  
  roi.append(m-1)
  regionsOfLine = []
  
  for x in range(len(roi)-1):
    pt1 = (roi[x], -1000)
    pt2 = (roi[x], 1000)
    cv2.line(staff, pt1, pt2, 120, 1, cv2.LINE_AA)
    subImage = staff[:, roi[x]:roi[x+1]]
    regionsOfLine.append(subImage)
    
  return staff, regionsOfLine


#def isLocalMax(array, i):
#  if i > 0:
#    if array[i] < array [i-1]:
#      return False
#  if i < len(array)-1:
#    if array[i] < array [i+1]:
#      return False
#  return True
#
#def findLHorizontalLines(projections):
#  thresh = max(projections)*0.4
#  lines = []
#  distances = []
#  for i in range(len(projections)):
#    if isLocalMax(projections, i) and projections[i]>thresh:
#      lines.append(i)
#      if len(lines)>0:
#        distances.append(i-lines[-1])
#  insideDistance = np.median(distances)
#  goodLines = []
#  for i in lines:
#    if lines[]
#  return insideDistance

  
  
if __name__ == '__main__':
  raw = cv2.imread('sheets/sheet2.png',0)
  n, m = raw.shape[:2]
  raw = cv2.adaptiveThreshold(raw, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 15, 5) 
#  raw = 255-rotateImage(255-raw, n, m, 50)
#  n, m = raw.shape[:2]
#  cv2.imshow('image1',raw)
  
  angle = findAngleWithHoughLines(255-raw, n, m)
#  print(angle)
#  projection(raw, 0)
  raw = 255-rotateImage(255-raw, n, m, angle)
  projections = projection(raw, 0)
  plt.plot(range(len(projections)), projections)
  cv2.imshow('image',(255-raw))
#  withBoxes, rects = getBoundingBoxes(raw, n, m)
#  cv2.imshow('boxes', withBoxes)  
  
  
  staves, staffLines = getSingleStaves(raw, n, m)
  numberOfLines = len(staffLines)
  i = 0
#  cv2.imshow("staff " + str(i), staves[i])
  staff, regionsOfLine = regions(staves, staffLines, i)
  cv2.imshow("a single line", regionsOfLine[8])
  
  cv2.waitKey(0) 
  cv2.destroyAllWindows()
  
  
  
  

