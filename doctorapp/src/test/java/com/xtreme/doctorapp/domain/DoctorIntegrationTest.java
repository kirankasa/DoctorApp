package com.xtreme.doctorapp.domain;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext*.xml")
@Transactional
@Configurable
@RooIntegrationTest(entity = Doctor.class)
public class DoctorIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private DoctorDataOnDemand dod;

	@Test
    public void testCountDoctors() {
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        Assert.assertTrue("Counter for 'Doctor' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindDoctor() {
        Doctor obj = dod.getRandomDoctor();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        Assert.assertNotNull("Find method for 'Doctor' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Doctor' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllDoctors() {
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        Assert.assertTrue("Too expensive to perform a find all test for 'Doctor', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Doctor> result = Doctor.findAllDoctors();
        Assert.assertNotNull("Find all method for 'Doctor' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Doctor' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindDoctorEntries() {
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        long count = Doctor.countDoctors();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Doctor> result = Doctor.findDoctorEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Doctor' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Doctor' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Doctor obj = dod.getRandomDoctor();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        Assert.assertNotNull("Find method for 'Doctor' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyDoctor(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Doctor' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        Doctor obj = dod.getRandomDoctor();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        boolean modified =  dod.modifyDoctor(obj);
        Integer currentVersion = obj.getVersion();
        Doctor merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Doctor' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", dod.getRandomDoctor());
        Doctor obj = dod.getNewTransientDoctor(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Doctor' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Doctor' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'Doctor' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        Doctor obj = dod.getRandomDoctor();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Doctor' failed to provide an identifier", id);
        obj = Doctor.findDoctor(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Doctor' with identifier '" + id + "'", Doctor.findDoctor(id));
    }
}
