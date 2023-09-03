a=[1,2,3,4,5]
b=[]
for i in range(0,len(a)):
    b.append(a[len(a)-i-1])
print(b)