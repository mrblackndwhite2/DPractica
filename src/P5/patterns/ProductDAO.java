package P5.patterns;

import P5.domein.*;

public interface ProductDAO {
    public boolean save(Product p);

    public boolean update(Product p);

    public boolean delete(Product p);
}
