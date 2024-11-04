package CheersMate.cheersmate.domain.controller;


import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.service.LiquorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/liquors")
public class LiquorController {

    @Autowired
    private LiquorService liquorService;

    @GetMapping
    public List<LiquorDTO> getAllLiquors() {
        return liquorService.getAllLiquors();
    }

    @GetMapping("/{id}")
    public Optional<LiquorDTO> getLiquorById(@PathVariable Long id) {
        return liquorService.getLiquorById(id);
    }

    @PostMapping
    public LiquorDTO createLiquor(@RequestBody LiquorDTO liquorDTO) {
        return liquorService.createLiquor(liquorDTO);
    }

    @PutMapping("/{id}")
    public LiquorDTO updateLiquor(@PathVariable Long id, @RequestBody LiquorDTO liquorDTO) {
        return liquorService.updateLiquor(id, liquorDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteLiquor(@PathVariable Long id) {
        liquorService.deleteLiquor(id);
        return "Liquor with ID " + id + " has been deleted.";
    }
}