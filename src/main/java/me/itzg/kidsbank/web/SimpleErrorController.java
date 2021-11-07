package me.itzg.kidsbank.web;

import java.util.Collections;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * This was introduced to intercept uncaught errors and redirect to the top level page.
 * <p>
 * HOWEVER, it become inelegant since it needs to hardcode understand API requests vs regular
 * requests.
 *
 * @author Geoff Bourne
 * @since Oct 2017
 */
@Controller
@Slf4j
public class SimpleErrorController implements ErrorController {
    private static final String[] attributesToFlash = new String[]{"status, error, path"};

    private final ErrorAttributes errorAttributes;

    @Autowired
    public SimpleErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<Object> handleError(WebRequest request, RedirectAttributes redirectAttributes,
                                              UriComponentsBuilder uriBuilder) {
        final Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(request,
            ErrorAttributeOptions.of(Include.EXCEPTION)
            );
        log.warn("Intercepted web controller error: request={}, error={}", request, errorAttributes);

        for (String name : attributesToFlash) {
            redirectAttributes.addFlashAttribute(name, errorAttributes.get(name));
        }

        final Object path = errorAttributes.get("path");
        final Object status = errorAttributes.get("status");
        if (path instanceof String && status instanceof Integer) {
            if (((String) path).startsWith("/api")) {
                return ResponseEntity.status((Integer) status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errorAttributes);
            } else if ("/".equals(path)) {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).location(
                uriBuilder.path("/")
                        .queryParam("error",
                                    errorAttributes.get("error"))
                        .queryParam("message",
                                    errorAttributes.get("message"))
                        .build(Collections.emptyMap()))
                .build();
    }
}
