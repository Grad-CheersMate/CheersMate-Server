package CheersMate.cheersmate.domain.controller;


import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.service.LiquorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class LiquorController {

    @Autowired
    private LiquorService liquorService;

    @GetMapping("api/liquors/category")
    public Page<LiquorDTO> getLiquorsByCategoryPaged(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return liquorService.getLiquorsByCategoryPaged(category, page, size);
    }

    @GetMapping("api/liquors/{id}")
    public Optional<LiquorDTO> getLiquorById(@PathVariable Long id) {
        return liquorService.getLiquorById(id);
    }

    @PostMapping("api/admin/liquors")
    public LiquorDTO createLiquor(@RequestBody LiquorDTO liquorDTO) {
        return liquorService.createLiquor(liquorDTO);
    }

    @PutMapping("api/admin/liquors/{id}")
    public LiquorDTO updateLiquor(@PathVariable Long id, @RequestBody LiquorDTO liquorDTO) {
        return liquorService.updateLiquor(id, liquorDTO);
    }

    @DeleteMapping("api/admin/liquors/{id}")
    public String deleteLiquor(@PathVariable Long id) {
        liquorService.deleteLiquor(id);
        return "Liquor with ID " + id + " has been deleted.";
    }
}