package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.repository.LiquorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LiquorService {

    private final LiquorRepository liquorRepository;

    public List<LiquorDTO> getAllLiquors() {
        return liquorRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LiquorDTO> getLiquorById(Long id) {
        return liquorRepository.findById(id).map(this::convertToDTO);
    }

    public void createLiquor(LiquorDTO liquorDTO) {
        Liquor liquor = convertToEntity(liquorDTO);
        liquor = liquorRepository.save(liquor);
        convertToDTO(liquor);
    }

    public void updateLiquor(Long id, LiquorDTO liquorDTO) {
        Optional<Liquor> liquorOptional = liquorRepository.findById(id);
        if (liquorOptional.isPresent()) {
            Liquor liquor = liquorOptional.get();
            liquor.setName(liquorDTO.getName());
            liquor.setAlcohol(liquorDTO.getAlcohol());
            liquor.setImageLink(liquorDTO.getImageLink());
            liquor.setCategory(liquorDTO.getCategory());
            liquor = liquorRepository.save(liquor);
            convertToDTO(liquor);
        }
    }

    public void deleteLiquor(Long id) {
        liquorRepository.deleteById(id);
    }

    private LiquorDTO convertToDTO(Liquor liquor) {
        LiquorDTO dto = new LiquorDTO();
        dto.setId(liquor.getId());
        dto.setName(liquor.getName());
        dto.setAlcohol(liquor.getAlcohol());
        dto.setImageLink(liquor.getImageLink());
        dto.setCategory(liquor.getCategory());
        return dto;
    }

    private Liquor convertToEntity(LiquorDTO liquorDTO) {
        Liquor liquor = new Liquor();
        liquor.setName(liquorDTO.getName());
        liquor.setAlcohol(liquorDTO.getAlcohol());
        liquor.setImageLink(liquorDTO.getImageLink());
        liquor.setCategory(liquorDTO.getCategory());
        return liquor;
    }

    public Page<LiquorDTO> getLiquorsByCategoryPaged(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findByCategory(category, pageable).map(this::convertToDTO);
    }

    // 검색 로직 추가
    public Page<LiquorDTO> searchLiquors(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findByNameContaining(keyword, pageable).map(this::convertToDTO);
    }

    public Page<LiquorDTO> getLiquorsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findAll(pageable).map(this::convertToDTO);
    }
}