class VendingMachine:
    def __init__(self, num_items, item_price):
        self.numItems = num_items
        self.itemPrice = item_price

    def buy(self, req_items, coins):

        if req_items > self.numItems:
            return "Not enough items in the machine"

        totalPrice = req_items * self.itemPrice

        if totalPrice > coins:
            return "Not enough coins"

        change = coins - (totalPrice)
        self.numItems -= req_items
        return change


vend = VendingMachine(1400, 2)
arrItemReq = [120, 50, 20, 30, 20, 40, 11]
arrcoins = [170, 125, 75, 32, 59, 140, 85]
expenses = 0

for i in range(len(arrItemReq)):
    result = vend.buy(arrItemReq[i], arrcoins[i])
    if type(result) is int:
        expenses += result

print(f"Your expense : {expenses}")
