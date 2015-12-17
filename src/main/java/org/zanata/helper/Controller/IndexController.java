package org.zanata.helper.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String hello(ModelMap model) {
        model.addAttribute("msg", "JCG Hello World!");
        return "index";
    }

    @RequestMapping(value = "/displayMessage/{msg}", method = RequestMethod.GET)
    public String displayMessage(@PathVariable String msg, ModelMap model) {
        model.addAttribute("msg", msg);
        return "index";
    }
}
