package docswave.test.sj.app.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <b>파일 설명</b>
 *
 * @author sejinpark
 * @since 20. 12. 7.
 */
@RestController
public class TestRest {
    @GetMapping("/")
    public String main() {
        return "main";
    }
}
