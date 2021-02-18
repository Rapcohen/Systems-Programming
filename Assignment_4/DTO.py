class Vaccine:
    def __init__(self, _id, _date, _supplier, _quantity):
        self.id = _id
        self.date = _date
        self.supplier = _supplier
        self.quantity = _quantity


class Supplier:
    def __init__(self, _id, _name, _logistic):
        self.id = _id
        self.name = _name
        self.logistic = _logistic


class Clinic:
    def __init__(self, _id, _location, _demand, _logistic):
        self.id=_id
        self.location = _location
        self.demand = _demand
        self.logistic = _logistic


class Logistic:
    def __init__(self, _id, _name, _count_sent, _count_received):
        self.id = _id
        self.name = _name
        self.count_sent = _count_sent
        self.count_received = _count_received
