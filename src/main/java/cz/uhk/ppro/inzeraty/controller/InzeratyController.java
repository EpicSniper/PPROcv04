package cz.uhk.ppro.inzeraty.controller;

import cz.uhk.ppro.inzeraty.model.Inzerat;
import cz.uhk.ppro.inzeraty.sluzby.UlozisteInzeratu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.stream.Collectors;

@Controller
public class InzeratyController {
    private UlozisteInzeratu ulozisteInzeratu;

    @Autowired
    public InzeratyController(UlozisteInzeratu ulozisteInzeratu) {
        this.ulozisteInzeratu = ulozisteInzeratu;
    }

    @GetMapping("/")
    public String redirect(){
        return "redirect:/inzeraty";
    }

    @GetMapping("/inzeraty")
    public String vypisInzeraty(@RequestParam(value = "kategorie", required = false) String kategorie, Model model) {
        if (kategorie != null) {
            model.addAttribute("inzeraty", ulozisteInzeratu.getInzeraty().stream().filter(
                    inzerat -> inzerat.getKategorie().equals(kategorie)).collect(Collectors.toList()));
        } else {
            model.addAttribute("inzeraty", ulozisteInzeratu.getInzeraty());
        }
        return "inzeraty";
    }

    @GetMapping("/inzeraty/new")
    public String vytvorInzerat(Model model) {
        Inzerat inzerat = new Inzerat();
        model.addAttribute("inzerat", inzerat);
        return "vytvor_inzerat";
    }

    @PostMapping("/heslo")
    public String ulozInzerat(@ModelAttribute("inzerat") Inzerat inzerat, Model model) {
        ulozisteInzeratu.pridej(inzerat);
        model.addAttribute("heslo", inzerat.getHesloProUpravu());

        return "heslo_inzeratu";
    }

    @PostMapping("/inzeraty")
    public String backFromHeslo() {
        return "redirect:/inzeraty";
    }

    @GetMapping("/inzeraty/edit/{id}")
    public String upravInzerat(@PathVariable Long id, @RequestParam(value = "heslo", required = false) String heslo, Model model) {
        if (heslo.equals(ulozisteInzeratu.getById(id.intValue()).getHesloProUpravu())) {
            model.addAttribute("inzerat", ulozisteInzeratu.getById(id.intValue()));
            return "uprav_inzerat";
        }

        return "redirect:/inzeraty";
    }

    @PostMapping(value = "/inzeraty/{id}", params = "zmenit")
    public String updateInzerat(@PathVariable Long id, @ModelAttribute("inzerat") Inzerat inzerat) {

        Inzerat oldInzerat = ulozisteInzeratu.getById(id.intValue());
        oldInzerat.setCena(inzerat.getCena());
        oldInzerat.setKategorie(inzerat.getKategorie());
        oldInzerat.setText(inzerat.getText());
        oldInzerat.setDatum(new Date());

        ulozisteInzeratu.uprav(oldInzerat);
        return "redirect:/inzeraty";
    }

    @RequestMapping(value = "/inzeraty/{id}", params = "smaz")
    public String smazInzerat(@PathVariable Long id, @ModelAttribute("inzerat") Inzerat inzerat) {
        ulozisteInzeratu.odstran(id.intValue());
        return "redirect:/inzeraty";
    }

    @RequestMapping("/inzeraty/potvrzeni/{id}")
    public String buttonPotvrd(@PathVariable Long id, Model model) {
        model.addAttribute(ulozisteInzeratu.getById(id.intValue()));
        return "potvrzeni_hesla";
    }
}
