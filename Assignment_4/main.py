import sys
from Repository import _Repository
import DTO


def main():
    repo = _Repository()
    repo.create_tables()

    # Parsing config file
    with open(sys.argv[1]) as config:
        lines = config.readlines()
        index = 1
        num_of_each = lines[0].split(",")
        vaccines = lines[index: index + int(num_of_each[0])]
        index = index + int(num_of_each[0])
        suppliers = lines[index:index + int(num_of_each[1])]
        index = index + int(num_of_each[1])
        clinics = lines[index: index + int(num_of_each[2])]
        index = index + int(num_of_each[2])
        logistics = lines[index:]
        for line in vaccines:
            line = line.replace("\n", "")
            args = line.split(",")
            repo.vac_dao.insert(DTO.Vaccine(int(args[0]), args[1], int(args[2]), int(args[3])))
        for line in suppliers:
            line = line.replace("\n", "")
            args = line.split(",")
            repo.sup_dao.insert(DTO.Supplier(int(args[0]), args[1], int(args[2])))
        for line in clinics:
            line = line.replace("\n", "")
            args = line.split(",")
            repo.clin_dao.insert(DTO.Clinic(int(args[0]), args[1], int(args[2]), int(args[3])))
        for line in logistics:
            line = line.replace("\n", "")
            args = line.split(",")
            repo.log_dao.insert(DTO.Logistic(int(args[0]), args[1], int(args[2]), int(args[3])))

    # Executing orders from orders file
    with open(sys.argv[2]) as orders, open(sys.argv[3], "w") as output:
        lines = orders.readlines()
        for line in lines:
            args = line.split(",")
            if len(args) == 3:
                repo.receive_ship(args[0], int(args[1]), args[2])
            else:
                repo.send_ship(args[0], int(args[1]))
            a, b, c, d = repo.create_record()
            output.write("{},{},{},{}\n".format(a[0], b[0], c[0], d[0]))

    repo.close()


if __name__ == '__main__':
    main()
