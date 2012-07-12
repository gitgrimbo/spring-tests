package grimbo.spring.test;

import java.util.ArrayList;
import java.util.List;

public class Bean {
    public static List<Bean> beans = new ArrayList<Bean>();

    String name;

    public Bean() {
        super();
        beans.add(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
