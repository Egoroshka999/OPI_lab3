package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "results", schema="s368100")
public class Result {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "x")
    private Double x;
    @Column(name = "y")
    private Double y;
    @Column(name = "r")
    private Double r;
    @Column(name = "result")
    private Boolean inside = false;
    
    public Result() {
        super();
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getX() {
        return x;
    }


    public void setY(Double y) {
        this.y = y;
    }

    public Double getY() {
        return y;
    }


    public void setR(Double r) {
        this.r = r;
    }

    public Double getR() {
        return r;
    }

    public boolean isInside() {
        return inside;
    }

    public boolean checkPoint() {
        boolean isInside = false;
        if (x <= 0 & y <= 0) {
            if (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r / 2, 2))
                isInside = true;
        }

        if (x >= 0 & y <= 0) {
            if (x - 2 * y <= r)
                isInside = true;
        }

        if (x <= 0 & y >= 0) {
            if (-x <= r && y <= r/2)
                isInside = true;
        }

        return isInside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

