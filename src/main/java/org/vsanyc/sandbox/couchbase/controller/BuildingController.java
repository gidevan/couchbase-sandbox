package org.vsanyc.sandbox.couchbase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vsanyc.sandbox.couchbase.entities.Building;
import org.vsanyc.sandbox.couchbase.service.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/building")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @PostMapping("/save")
    public Building save(@RequestBody Building building) {
        return buildingService.save(building);
    }

    @GetMapping("/{buildingId}")
    public Building findById(@PathVariable String buildingId) {
        return buildingService.findById(buildingId);
    }

    @GetMapping("/company/{companyId}")
    public List<Building> findByCompanyId(@PathVariable String companyId) {
        return buildingService.findByCompanyId(companyId);
    }

    @GetMapping("/company/{companyId}/area/{areaId}")
    public Building findByCompanyAndAreaId(String companyId, String areaId) {
        return buildingService.findByCompanyAndAreaId(companyId, areaId);
    }

    @GetMapping("/company/{companyId}/name/{name}/page/{page}")
    public List<Building> findByCompanyIdAndNameLike(@PathVariable String companyId, @PathVariable String name,
                                                     @PathVariable int page) {
        return buildingService.findByCompanyIdAndNameLike(companyId, name, page);
    }

    @GetMapping("/telephone/{telephoneNumber}")
    public List<Building> findByPhoneNumber(String telephoneNumber) {
        return buildingService.findByPhoneNumber(telephoneNumber);
    }

    @GetMapping("/company/{companyId}/count")
    public Long countBuildings(String companyId) {
        return buildingService.countBuildings(companyId);
    }
}
