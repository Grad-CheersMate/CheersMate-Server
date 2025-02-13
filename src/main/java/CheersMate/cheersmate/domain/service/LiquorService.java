package CheersMate.cheersmate.domain.service;

import CheersMate.cheersmate.domain.dto.LiquorDTO;
import CheersMate.cheersmate.domain.entity.Liquor;
import CheersMate.cheersmate.domain.mapper.LiquorMapper;
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
    private final LiquorMapper liquorMapper;

    public List<LiquorDTO> getAllLiquors() {
        return liquorRepository.findAll().stream()
                .map(liquorMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<LiquorDTO> getLiquorById(Long id) {
        return liquorRepository.findById(id)
                .map(liquorMapper::toDto);
    }

    public void createLiquor(LiquorDTO liquorDTO) {
        Liquor liquor = liquorMapper.toEntity(liquorDTO); // DTO -> Entity
        liquorRepository.save(liquor);
    }

    public void updateLiquor(Long id, LiquorDTO liquorDTO) {
        Optional<Liquor> liquorOpt = liquorRepository.findById(id);
        if (liquorOpt.isPresent()) {
            Liquor liquor = liquorOpt.get();

            liquor.setName(liquorDTO.getName());
            liquor.setAlcohol(liquorDTO.getVolume());
            liquor.setImageLink(liquorDTO.getImageUrl());
            liquor.setCategory(liquorDTO.getCategory());
            liquorRepository.save(liquor);
        }
    }

    public void deleteLiquor(Long id) {
        liquorRepository.deleteById(id);
    }

    public Page<LiquorDTO> getLiquorsByCategoryPaged(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findByCategory(category, pageable)
                .map(liquorMapper::toDto);
    }

    public Page<LiquorDTO> searchLiquors(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findByNameContaining(keyword, pageable)
                .map(liquorMapper::toDto);
    }

    public Page<LiquorDTO> getLiquorsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return liquorRepository.findAll(pageable)
                .map(liquorMapper::toDto);
    }
}