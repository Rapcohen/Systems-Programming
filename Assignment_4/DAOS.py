class Vaccines:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, vaccine):
        self._conn.execute("""
               INSERT INTO vaccines (id, date, supplier, quantity) VALUES (?,?,?,?)
           """, [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])

    # Sends "amount" of vaccines from the distribution center - taking the oldest first
    def send_amount(self, amount):
        c = self._conn.cursor()
        c.execute("""
        SELECT * FROM vaccines ORDER BY date 
        """)
        count = amount
        while count > 0:
            row = c.fetchone()
            if count < row[3]:
                new_quan = row[3] - count
                self._conn.execute("""
                UPDATE vaccines SET quantity=? WHERE id=?
                """, [new_quan, row[0]])
            else:
                self._conn.execute("""
                DELETE FROM vaccines WHERE id=?
                """, [row[0]])
            count -= row[3]

    def total_inventory(self):
        c = self._conn.cursor()
        c.execute("""
        SELECT SUM(quantity) FROM vaccines
        """)
        result = c.fetchone()
        return result

    def max_id(self):
        c = self._conn.cursor()
        c.execute("""
        SELECT MAX(id) FROM vaccines
        """)
        result = c.fetchone()
        return result


class Suppliers:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, supplier):
        self._conn.execute("""
               INSERT INTO suppliers (id, name, logistic) VALUES (?, ?, ?)
           """, [supplier.id, supplier.name, supplier.logistic])

    def get_sup_id(self, name):
        c = self._conn.cursor()
        c.execute("""
        SELECT * FROM suppliers WHERE name=?
        """, [name])
        result = c.fetchone()
        return result[0]

    def get_log_id(self, _id):
        c = self._conn.cursor()
        c.execute("""
        SELECT * FROM suppliers WHERE id=?
        """, [_id])
        result = c.fetchone()
        return result[2]


class Clinics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, clinic):
        self._conn.execute("""
               INSERT INTO clinics (id, location, demand, logistic) VALUES (?, ?, ?, ?)
           """, [clinic.id, clinic.location, clinic.demand, clinic.logistic])

    # Reduces the demand from the specified "location" by "amount"
    def reduce_demand(self, location, amount):
        prev_demand = self.get_demand(location)
        demand = prev_demand - amount
        self._conn.execute("""
        UPDATE clinics SET demand=? WHERE location=?
        """, [demand, location])

    def get_log_id(self, location):
        c = self._conn.cursor()
        c.execute("""
        SELECT * FROM clinics WHERE location=?
        """, [location])
        result = c.fetchone()
        return result[3]

    def get_demand(self, location):
        c = self._conn.cursor()
        c.execute("""
        SELECT * FROM clinics WHERE location=?
        """, [location])
        result = c.fetchone()
        return result[2]

    def total_demand(self):
        c = self._conn.cursor()
        c.execute("""
        SELECT SUM(demand) FROM clinics
        """)
        result = c.fetchone()
        return result


class Logistics:
    def __init__(self, conn):
        self._conn = conn

    def insert(self, logistic):
        self._conn.execute("""
               INSERT INTO logistics (id, name, count_sent, count_received) VALUES (?, ?, ?, ?)
           """, [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])

    def update_count_s(self, _id, amount):
        prev_count = self.get_current_count(_id, 2)
        amount += prev_count
        self._conn.execute("""
        UPDATE logistics SET count_sent={} WHERE id={}
        """.format(amount, _id))

    def update_count_r(self, _id, amount):
        prev_count = self.get_current_count(_id, 3)
        amount += prev_count
        self._conn.execute("""
        UPDATE logistics SET count_received={} WHERE id={}
        """.format(amount, _id))

    def get_current_count(self, _id, col_num):
        c = self._conn.cursor()
        c.execute("""
                SELECT * FROM logistics WHERE id=?
                """, [_id])
        result = c.fetchone()
        return result[col_num]

    def total_sent(self):
        c = self._conn.cursor()
        c.execute("""
        SELECT SUM(count_sent) FROM logistics
        """)
        result = c.fetchone()
        return result

    def total_received(self):
        c = self._conn.cursor()
        c.execute("""
        SELECT SUM(count_received) FROM logistics
        """)
        result = c.fetchone()
        return result
