import sys
import random
import math

# print to stderr for debugging purposes
# remove all debugging statements before submitting your code
s = sys.argv[1]
msg = "Given board " + s + "\n";
sys.stderr.write(msg);

#parse the input string, i.e., argv[1]
#pasing the board 
board = (s.split("LastPlay:")[0]).split("][")
L = len(board)-1
board[0]=board[0][1:]; board[L]=board[L][:-1]

#parsing the last move and transfer it into the 2-D coordinate
lastmove = s.split("LastPlay:")[1];
finalmove = ""      #The output move
M = []              #The output coordinate
D = 3               #The max depth the minimax traversing can reach

if (lastmove == "null"):
    M = [1,(L//2)+1, len(board[(L//2)+1])//2]          #Script goes first
else:
    lastmove = [L-int(lastmove[3]),int(lastmove[5])]   #Default goes first
    
#perform intelligent search to determine the next move

#Given a specific move, return the list of all possible move
def possiblemove(board,move):
    x = move[0]
    y = move[1]
    P = []      #The empty lise that will store
                #all the coordinates of the possible moves
    if(board[x-1][y-1]=='0'):P += [[x-1,y-1]];
    if(board[x-1][y]=='0'):P += [[x-1,y]];
    if(board[x][y-1]=='0'):P += [[x,y-1]];
    if(board[x][y+1]=='0'):P += [[x,y+1]];

    #If the specific move refers to the last second rows(the longest row)
    #then the exception will treat the last row as a row with length -1
    if(x == L-1):
        if(board[x+1][y-1]=='0'):P += [[x+1,y-1]];
        if(board[x+1][y]=='0'):P += [[x+1,y]];
    else:
        if(board[x+1][y]=='0'):P += [[x+1,y]];
        if(board[x+1][y+1]=='0'):P += [[x+1,y+1]];

    #If all the surrounded moves have been taken, it will search the entire
    #board and returns all the free moves('0')
    if(len(P)==0):
        for i in range(0,len(board)):
            for j in range(0,len(board[i])):
                if (board[i][j] == '0'):
                    P += [[i,j]]
    
    return P

#Return the combination list surrounded the specific move
#There are only three possible combinations(1,2),(1,3),(2,3)
#So the maximum length of the list will be 3
def combinationCount(board,move):
    x = move[0]
    y = move[1]

    C = [[int(board[x-1][y-1]),int(board[x-1][y])]]+\
        [[int(board[x-1][y]),int(board[x][y+1])]]+\
        [[int(board[x][y-1]),int(board[x-1][y-1])]]

    #Similiarly, if the specific move is at the last second row(longest row),
    #it will treat the row below it as the row with length-1
    if (x == L-1):
        C +=[[int(board[x][y+1]),int(board[x+1][y])]]
        C +=[[int(board[x+1][y]),int(board[x+1][y-1])]]
        C +=[[int(board[x+1][y-1]),int(board[x][y-1])]]
    else:
        C +=[[int(board[x][y+1]),int(board[x+1][y+1])]]
        C +=[[int(board[x+1][y+1]),int(board[x+1][y])]]
        C +=[[int(board[x+1][y]),int(board[x][y-1])]]

    #remove all the duplicates in the list
    #and the removes all the combinations that has a free move in it('0')
    C = removeDup([tuple(sorted(t)) for t in C if not 0 in t])

    return C


#The evaluation wrapper
def evaluator(board,move):
    B = board
    x = move[0]
    y = move[1]

    #number of free moves 
    NP = len(possiblemove(board,move))

    #number of combinations
    NC = 100*len(combinationCount(board,move))

    return NP+NC

#Determine if a move node is a leaf or not
#Notice we don't have to consider the draw situation since the Sperner's Lemma
#reveals that there is always a winner.
def isLeaf(board,move):
    if len(combinationCount(board,move))==3:
        return True
    return False


#Given the combination list, it randomly returns a availiable color
def pickcolor(board,move):
    C = combinationCount(board,move)
    P = [1,2,3]
    for t in C:
        if t ==(1,2):P.remove(3)
        if t ==(1,3):P.remove(2)
        if t ==(2,3):P.remove(1)

    if(len(P)==0):
        return 0     #NO COLORS = LOST! This line is only for completation
    else:
   	return random.choice(P)   #return a random avaliable color
    

#Perform the first minimax calculation and set the max depth
def choosemove(board,move):
    minUni = 1000
    bestmove = [0,0,0]
    P = possiblemove(board,move)
    for m in P:
        #set the evaluated move
        c = pickcolor(board,m)
        x = m[0]
        y = m[1]

        #do the move
        board[x] = str(int(board[x])+int(c*(math.pow(10,len(board[x])-y-1))))

        #calculate the point recursivly and compare it with the local min
        val = minMax(board,move,1)
        if(val < minUni):
            bestmove = [c,x,y]
            minUni = val

        #undo the move
        board[x] = str(int(board[x])-int(c*(math.pow(10,len(board[x])-y-1))))
    return bestmove

def minMax(board,move,depth):
    #base situation for recursion
    if (isLeaf(board,move)) or (depth == D):
        return evaluator(board,move)
    elif((depth%2)==0):
        minLocal = 1000
        P = possiblemove(board,move)
        for m in P:
            #set the evaluated move
            c = pickcolor(board,m)
            x = m[0]
            y = m[1]

            #do the move
            board[x] = str(int(board[x])+int(c*(math.pow(10,len(board[x])-y-1))))

            #calculate the point recursivly and compare it with the local min
            val = minMax(board,move,depth+1)
            if(val < minLocal):
		minLocal = val

            #undo the move
            board[x] = str(int(board[x])-int(c*(math.pow(10,len(board[x])-y-1))))
        return minLocal
    else:
        maxLocal = -1000
        P = possiblemove(board,move)
        for m in P:
            #set the evaluated move
            c = pickcolor(board,m)
            x = m[0]
            y = m[1]

            #do the mvoe
            board[x] = str(int(board[x])+int(c*(math.pow(10,len(board[x])-y-1))))

            #calculate the point recursivly and compare it with the local max
            val = minMax(board,move,depth+1)
            if(val > maxLocal):
		maxLocal = val

            #undo the move
            board[x] = str(int(board[x])-int(c*(math.pow(10,len(board[x])-y-1))))
        return maxLocal


#helper method for combinaitonCount,
##remove all the duplicated in the combination list            
def removeDup(seq):
    seen = set()
    seen_add = seen.add
    return [ x for x in seq if not (x in seen or seen_add(x))]


#The overall wrapper
if (lastmove == "null"):
    M = M + [len(board[M[1]])-1-M[2]]    #Script goes first
else:
    M = choosemove(board,lastmove)       #Default goes first 
    M = M + [len(board[M[1]])-1-M[2]]    #Add the last coordiante z

#Transfer the 2-D coordinate to (c,x,y,z)
finalmove = "("+str(M[0])+","+str(L-M[1])+","+str(M[2])+","+str(M[3])+")"

    
#print to stdout for AtroposGame
sys.stdout.write(finalmove);

