package nl.novi.cardemo.controllers;

import nl.novi.cardemo.models.Car;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {
    private List<Car> carList = new ArrayList<>();
    private Long currentId = 1L;

    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        car.setId(currentId++);
        carList.add(car);

        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    private Car findCarById(Long id) {
        for (Car car : carList) {
            if (car.getId() == id) {
                return car;
            }
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCar(@PathVariable Long id) {
        var optionalCar = findCarById(id);
        if (optionalCar == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(optionalCar);
    }

    private List<Car> getFilteredCars(String brand){
        List<Car> filteredCars = new ArrayList<>();
        for (Car car : carList) {
            if (car.getBrand().equals(brand)){
                filteredCars.add(car);
            }
        }
        return filteredCars;
    }

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars(@RequestParam(name = "brand", required = false) String brand) {
        if (brand != null && !brand.isEmpty()) {
            List<Car> filteredCars = getFilteredCars(brand);
            return ResponseEntity.ok(filteredCars);
        } else {
            return ResponseEntity.ok(carList);
        }
    }
}
