package model;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Named("bean")
@ApplicationScoped
public class PointBean {


    @PostConstruct
    public void init() {
        x = 0d;
        y = 0d;
        r = 2.0;
    }

    private Double x;
    private Double y;
    private Double r;

    private List<Result> results = new ArrayList<>();
    private Result lastCheckedPoint;

    public PointBean() {
        super();
        results = new ArrayList<>();
        try {
            results = new ArrayList<>(DAOFactory.getInstance().getResultDAO().getAllResults());
        } catch (SQLException ignored) {}
    }

    public List<Result> getResults() {
        return results;
    }

    public Result getLastCheckedPoint() {
        if (lastCheckedPoint == null) {
            lastCheckedPoint = new Result();
        }
        return lastCheckedPoint;
    }                                  

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getR() {
        return r;
    }

    public void setR(Double r) {
        this.r = r;
    }

    public void checkPoint() {
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

        Result result = new Result();
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setInside(isInside);
        try {
            DAOFactory.getInstance().getResultDAO().save(result);
        } catch (Exception e) {
            System.err.println(e);
        }
        results.add(result);
        lastCheckedPoint = new Result();
        lastCheckedPoint.setX(x);
        lastCheckedPoint.setY(y);
        lastCheckedPoint.setR(r);
        lastCheckedPoint.setInside(isInside);
    }

    @Override
    public String toString() {
        return "x=" + x +
                ", y=" + y +
                ", a="+ lastCheckedPoint.isInside();
    }
}
