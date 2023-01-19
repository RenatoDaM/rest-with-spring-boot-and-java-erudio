package com.example.restwithspringbootandjavaerudio;

import com.example.restwithspringbootandjavaerudio.exceptions.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MathController {

    private static final String template = "Hello %s!";
    private static final AtomicLong counter = new AtomicLong();

    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    //é usado o request param por que é obrigatório e os parametros são passados no path /parametro/parametro,
    // caso não seja passado a aplicação irá "quebrar", diferente de query param
    public double sum(
            // Vamos usar PathVariable popr ser RequestParam e não QueryParam
            @PathVariable(value = "numberOne") String numberOne,
            @PathVariable(value = "numberTwo") String numberTwo) throws Exception {
        if(!isNumeric(numberOne) || !isNumeric(numberTwo)) {
            throw new ResourceNotFoundException("Please set a numeric value");
        }
        return convertToDouble(numberOne) + convertToDouble(numberTwo);
    }

    private Double convertToDouble(String strNumber) {
        if (strNumber == null) return 0D;
        //BR 10,25 US 10.25
        String number = strNumber.replaceAll(",", ".");
        if (isNumeric(number)) return Double.parseDouble(number);
        return 0D;
    }

    private boolean isNumeric(String strNumber) {
        if (strNumber == null) return false;
        String number = strNumber.replaceAll(",", ".");
        return number.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

}
