import cv2, pickle
import numpy as np
#import matplotlib.pyplot as plt

def findAngleWithHoughLines(img, n, m):
  img = cv2.Canny(img,50, 200, 3)
  lines = cv2.HoughLines(img,0.5,np.pi/720,m//5)
  thetas = []
  for line in lines:
    theta = line[0][1]
    thetas.append(theta-np.pi/2)
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
  # Project a binary image on an axis making a certain angle
  # with the horizontal axis
  n, m = img.shape[:2]
  projVector = np.array([np.cos(angle), np.sin(angle)])
  print(n, m, projVector)
  corners = [(0,0), (n-1,0), (n-1,m-1), (0,m-1)]
#  corners = [(0,0), (m-1,0), (m-1,n-1), (0,n-1)]
  majorPoints = sorted([np.dot(corner, projVector) for corner in corners])
  print(majorPoints)
  Max, Min = majorPoints[3], majorPoints[0]
  print(Max, Min)
  projections = [0]*int(Max - Min+1)
  for i in range(n):
    for j in range(m):
      projections[int(np.dot(np.array([i,j]), projVector)-Min)] += 1-img[i][j]/255.0
  return projections


# 2 methods that find which angle gives the highest possible value
# for projection and find optimal angle. Computationally expensive and not robust.
def findAngleWithProjection(img, n, m):
  p, maxval = 0, 0
  for i in range(360):
    val = max(projection(img, i*np.pi/360))
    if val>maxval:
      maxval = val
      p = i
  return np.pi*p / 2

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

# 2 utility functions to find maximums of array of numbers
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


# Get single staff lines from an image
def getSingleStaves(img, n, m):
  #Hough lines for whole image
  canny = cv2.Canny(img,50, 200, 3)
  lines = cv2.HoughLines(canny,5 ,np.pi/360,m//5)
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

# Draw lines on a single staff line
def drawStaffLines(staves, staffLines, i):

  staff = staves[i]
  staffLine = staffLines[i]
  for y in staffLine:
    pt1 = (1000, int(y))
    pt2 = (-1000, int(y))
    cv2.line(staff, pt1, pt2, 120, 1, cv2.LINE_AA)

  return staff


# For a staff line, get regions with symbols.
def regions(staves, staffLines, j):

  staff = staves[j]
  staffLine = staffLines[j]

  projections = projection(staff, np.pi/2)

#  plt.plot(range(len(projections)), projections)
#  plt.plot(range(len(projections)), [sum(projections)/len(projections)]*len(projections))

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


# For a region, get height of most
def getHeight(regionsOfLine, staffLines, j):

  region = regionsOfLine[j]

  projections = projection(region, 0)

  print

  dx = int(2 * (staffLines[0][1] - staffLines[0][0]))
#  convOp = [ convDistance//2 + min(i, convDistance - i) for i in range(convDistance)]
  gx = np.arange(-5, 5)
  convOp = np.exp(-(gx/dx)**2/2)
#  print len(projections)
  projections = np.convolve(projections, convOp, "same")
#  print len(projections)

  maxIndex = np.argmax(projections)

  pt1 = (1000, maxIndex)
  pt2 = (-1000, maxIndex)

  cv2.line(regionsOfLine[j], pt1, pt2, 120, 1, cv2.LINE_AA)
  projSums = np.cumsum(projections)
#  plt.plot(range(len(projSums)), projections)

  return region


if __name__ == '__main__':
    # Open and adaptThreshold image
    raw = cv2.imread('./sheets/sheet5.png',0)
    n, m = raw.shape[:2]
    raw = cv2.adaptiveThreshold(raw, 255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY, 15, 5)

    # Rotate image for fun
    #  raw = 255-rotateImage(255-raw, n, m, 50)
    #  n, m = raw.shape[:2]
    #  cv2.imshow('image1',raw)

    # Rotate image to make it horizontal
    angle = findAngleWithHoughLines(255-raw, n, m)
    raw = 255-rotateImage(255-raw, n, m, angle)
    n, m = raw.shape[:2]

    # Get arrays of staves and staffLines (equation)
    staves, staffLines = getSingleStaves(raw, n, m)
    numberOfLines = len(staffLines)

    # Show staff line index i
    i = 0
    cv2.imshow("a single line ", staves[i])

    # Get j-th component on i-th line and show it
    staff, regionsOfLine = regions(staves, staffLines, i)
    j = 14
    getHeight(regionsOfLine, staffLines, j) # detect height of note
    cv2.imshow("a region", regionsOfLine[j])

    # Importing classifiers and categories
    sym_class_path = "./symbols_classifier.sav"
    clef_class_path = "./clefmodel.sav"
    sym_class = pickle.load(open(sym_class_path, "rb"))
    clef_class = pickle.load(open(clef_class_path, "rb"))
    cats = pickle.load(open("categories.sav", "rb"))
    n_symbs = len(cats)
    clefs = pickle.load(open("clefs.sav", "rb"))
    n_clefs = len(clefs)

    list_of_regions = []

    kernel = np.ones((3,1), np.uint8)

    for i in range(len(regionsOfLine)):
        regionsOfLine[i] = cv2.dilate(regionsOfLine[i], kernel, iterations=1)
        regionsOfLine[i] = cv2.erode(regionsOfLine[i], kernel, iterations=1)
        cv2.imshow('aaaa',regionsOfLine[10])
        list_of_regions.append(255 - regionsOfLine[i])
        list_of_regions[-1] = cv2.resize(list_of_regions[-1], (20,20))

    list_of_regions = np.array(list_of_regions)
    list_of_regions = list_of_regions.reshape(list_of_regions.shape[0],20,20,1)

    first = list_of_regions[:1,:,:,:]
    rest = list_of_regions[1:,:,:,:]

    # Prediction
    clef = clef_class.predict(first)
    syms = sym_class.predict(rest)

    # Translating results
    clef_res = ""
    sym_res = [""]*syms.shape[0]
    for i in range(syms.shape[0]):
        y = syms[i,:]
        m, k = 0, 0
        for j in range(n_symbs):
            if (y[j]>m):
                m = y[j]
                k = j
        sym_res[i] = cats[k]
    if (clef[0,0] > clef[0,1]):
        clef_res = clefs[1]
    else:
        clef_res = clefs[0]

    print(sym_res)
    print(len(rest), " is length")
    print(clef_res)

    cv2.waitKey(0)
    cv2.destroyAllWindows()
