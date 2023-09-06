def solution(a):
    for i in a:
        largest=i[0]
        for j in i:
            if j>largest:
                largest=j
        yield largest

def main():
    a = [[11, 2, 3], [1, 2, 3, 4], [1, 2, 3]]
    for i in solution(a):
        print(i)    

main()