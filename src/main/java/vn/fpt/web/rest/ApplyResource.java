package vn.fpt.web.rest;

import vn.fpt.domain.Apply;
import vn.fpt.service.ApplyService;
import vn.fpt.web.rest.errors.BadRequestAlertException;
import vn.fpt.service.dto.ApplyCriteria;
import vn.fpt.service.ApplyQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.fpt.domain.Apply}.
 */
@RestController
@RequestMapping("/api")
public class ApplyResource {

    private final Logger log = LoggerFactory.getLogger(ApplyResource.class);

    private static final String ENTITY_NAME = "apply";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ApplyService applyService;

    private final ApplyQueryService applyQueryService;

    public ApplyResource(ApplyService applyService, ApplyQueryService applyQueryService) {
        this.applyService = applyService;
        this.applyQueryService = applyQueryService;
    }

    /**
     * {@code POST  /applies} : Create a new apply.
     *
     * @param apply the apply to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new apply, or with status {@code 400 (Bad Request)} if the apply has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/applies")
    public ResponseEntity<Apply> createApply(@RequestBody Apply apply) throws URISyntaxException {
        log.debug("REST request to save Apply : {}", apply);
        if (apply.getId() != null) {
            throw new BadRequestAlertException("A new apply cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Apply result = applyService.save(apply);
        return ResponseEntity.created(new URI("/api/applies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /applies} : Updates an existing apply.
     *
     * @param apply the apply to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated apply,
     * or with status {@code 400 (Bad Request)} if the apply is not valid,
     * or with status {@code 500 (Internal Server Error)} if the apply couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/applies")
    public ResponseEntity<Apply> updateApply(@RequestBody Apply apply) throws URISyntaxException {
        log.debug("REST request to update Apply : {}", apply);
        if (apply.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Apply result = applyService.save(apply);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, apply.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /applies} : get all the applies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of applies in body.
     */
    @GetMapping("/applies")
    public ResponseEntity<List<Apply>> getAllApplies(ApplyCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Applies by criteria: {}", criteria);
        Page<Apply> page = applyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /applies/count} : count all the applies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/applies/count")
    public ResponseEntity<Long> countApplies(ApplyCriteria criteria) {
        log.debug("REST request to count Applies by criteria: {}", criteria);
        return ResponseEntity.ok().body(applyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /applies/:id} : get the "id" apply.
     *
     * @param id the id of the apply to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the apply, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/applies/{id}")
    public ResponseEntity<Apply> getApply(@PathVariable Long id) {
        log.debug("REST request to get Apply : {}", id);
        Optional<Apply> apply = applyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(apply);
    }

    /**
     * {@code DELETE  /applies/:id} : delete the "id" apply.
     *
     * @param id the id of the apply to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/applies/{id}")
    public ResponseEntity<Void> deleteApply(@PathVariable Long id) {
        log.debug("REST request to delete Apply : {}", id);
        applyService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
