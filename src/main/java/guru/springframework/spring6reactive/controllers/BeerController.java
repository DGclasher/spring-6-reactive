package guru.springframework.spring6reactive.controllers;

import guru.springframework.spring6reactive.model.BeerDTO;
import guru.springframework.spring6reactive.services.BeerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by jt, Spring Framework Guru.
 */
@RestController
@RequiredArgsConstructor
public class BeerController {

    public static final String BEER_PATH = "/api/v2/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final BeerService beerService;

    @DeleteMapping(BEER_PATH_ID)
    public Mono<ResponseEntity<Void>> deleteBeer(@PathVariable Integer beerId) {
        return beerService.deleteById(beerId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PatchMapping(BEER_PATH_ID)
    Mono<ResponseEntity<Void>> patchExistingBeer(@PathVariable Integer beerId,
                                                 @RequestBody @Validated BeerDTO beerDTO){
        return beerService.patchBeer(beerId, beerDTO)
                .map(updatedDto -> ResponseEntity.ok().build());
    }

    @PutMapping(BEER_PATH_ID)
    Mono<ResponseEntity<Void>> updateExistingBeer(
            @PathVariable Integer beerId,
            @RequestBody @Validated BeerDTO beerDTO) {

        return beerService.updateBeer(beerId, beerDTO)
                .map(beer -> new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    @PostMapping(BEER_PATH)
    Mono<ResponseEntity<Void>> createNewBeer(@RequestBody @Validated BeerDTO beerDTO){
       return beerService.saveNewBeer(beerDTO)
               .map(savedDto -> ResponseEntity.created(UriComponentsBuilder
                       .fromHttpUrl("http://localhost:8080/" + BEER_PATH
                               + "/" + savedDto.getId())
                       .build().toUri())
                       .build());
    }

    @GetMapping(BEER_PATH_ID)
    Mono<ResponseEntity<BeerDTO>> getBeerById(@PathVariable("beerId") Integer beerId){
        return beerService.getBeerById(beerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping(BEER_PATH)
    Flux<BeerDTO> listBeers(){
        return beerService.listBeers();
    }

}
