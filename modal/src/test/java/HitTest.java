import model.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

public class HitTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy");
    @Test
    @DisplayName("Hit test")
    public void test() {
        Result result = new Result();
        result.setX(-1.0);
        result.setY(1.0);
        result.setR(4.0);
        Assertions.assertTrue(result.checkPoint());
    }
}
