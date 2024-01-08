package org.vertx;

public class Student {
    private Integer id;
    private String name;
    private Double a;
    private Double b;
    private Double c;

    public Student(Integer id, String name, Double a, Double b, Double c) {
        this.id = id;
        this.name = name;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getA() {
        return a;
    }

    public void setA(Double a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Double getC() {
        return c;
    }

    public void setC(Double c) {
        this.c = c;
    }
}
