package me.itzg.kidsbank.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Controller
@Slf4j
public class SimpleErrorController implements ErrorController {
    private static final String[] attributesToFlash = new String[]{"status, error, path"};

    private ErrorAttributes errorAttributes;

    @Autowired
    public SimpleErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(WebRequest request, RedirectAttributes redirectAttributes) {
        final Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(request, true);
        log.warn("Intercepted web controller error: request={}, error={}", request, errorAttributes);

        for (String name : attributesToFlash) {
            redirectAttributes.addFlashAttribute(name, errorAttributes.get(name));
        }

        return "redirect:/";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
