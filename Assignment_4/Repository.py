import sqlite3
import DAOS
import DTO


class _Repository:
    def __init__(self):
        self._conn = sqlite3.connect('database.db')
        self.vac_dao = DAOS.Vaccines(self._conn)
        self.sup_dao = DAOS.Suppliers(self._conn)
        self.clin_dao = DAOS.Clinics(self._conn)
        self.log_dao = DAOS.Logistics(self._conn)

    def close(self):
        self._conn.commit()
        self._conn.close()

    def create_tables(self):
        self._conn.executescript("""
        CREATE TABLE vaccines (
            id INTEGER PRIMARY KEY,
            date DATE NOT NULL,
            supplier INTEGER REFERENCES suppliers(id),
            quantity INTEGER NOT NULL 
        );
            
        CREATE TABLE suppliers (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            logistic INTEGER REFERENCES logistics(id)
        );
        
        CREATE TABLE clinics (
            id INTEGER PRIMARY KEY,
            location TEXT NOT NULL,
            demand INTEGER NOT NULL,
            logistic INTEGER REFERENCES logistics(id)
        );
        
        CREATE TABLE logistics (
            id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            count_sent INTEGER NOT NULL,
            count_received INTEGER NOT NULL
        );
        """)

    def receive_ship(self, _name, amount, date):
        sup_id = self.sup_dao.get_sup_id(_name)
        max_id = self.vac_dao.max_id()
        self.vac_dao.insert(DTO.Vaccine(max_id[0]+1, date, sup_id, amount))
        log_id = self.sup_dao.get_log_id(sup_id)
        self.log_dao.update_count_r(log_id, amount)

    def send_ship(self, location, amount):
        self.clin_dao.reduce_demand(location, amount)
        self.vac_dao.send_amount(amount)
        log_id = self.clin_dao.get_log_id(location)
        self.log_dao.update_count_s(log_id, amount)

    def create_record(self):
        t_inv = self.vac_dao.total_inventory()
        t_demand = self.clin_dao.total_demand()
        t_rec = self.log_dao.total_received()
        t_sent = self.log_dao.total_sent()
        return t_inv, t_demand, t_rec, t_sent
