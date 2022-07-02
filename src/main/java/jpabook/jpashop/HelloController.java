package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {

        // Model 에 데이터를 실어서 Controller 에서 View 로 넘겨 줄 수 있다.
        model.addAttribute("data", "hello!!!");

        // return 은 화면 이름이다.
        return "hello";
    }
}
