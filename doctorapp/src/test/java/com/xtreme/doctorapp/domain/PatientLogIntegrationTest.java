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
@RooIntegrationTest(entity = PatientLog.class)
public class PatientLogIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    private PatientLogDataOnDemand dod;

	@Test
    public void testCountPatientLogs() {
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", dod.getRandomPatientLog());
        long count = PatientLog.countPatientLogs();
        Assert.assertTrue("Counter for 'PatientLog' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindPatientLog() {
        PatientLog obj = dod.getRandomPatientLog();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to provide an identifier", id);
        obj = PatientLog.findPatientLog(id);
        Assert.assertNotNull("Find method for 'PatientLog' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'PatientLog' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllPatientLogs() {
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", dod.getRandomPatientLog());
        long count = PatientLog.countPatientLogs();
        Assert.assertTrue("Too expensive to perform a find all test for 'PatientLog', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<PatientLog> result = PatientLog.findAllPatientLogs();
        Assert.assertNotNull("Find all method for 'PatientLog' illegally returned null", result);
        Assert.assertTrue("Find all method for 'PatientLog' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindPatientLogEntries() {
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", dod.getRandomPatientLog());
        long count = PatientLog.countPatientLogs();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<PatientLog> result = PatientLog.findPatientLogEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'PatientLog' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'PatientLog' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        PatientLog obj = dod.getRandomPatientLog();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to provide an identifier", id);
        obj = PatientLog.findPatientLog(id);
        Assert.assertNotNull("Find method for 'PatientLog' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPatientLog(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'PatientLog' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        PatientLog obj = dod.getRandomPatientLog();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to provide an identifier", id);
        obj = PatientLog.findPatientLog(id);
        boolean modified =  dod.modifyPatientLog(obj);
        Integer currentVersion = obj.getVersion();
        PatientLog merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'PatientLog' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", dod.getRandomPatientLog());
        PatientLog obj = dod.getNewTransientPatientLog(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'PatientLog' identifier to be null", obj.getId());
        obj.persist();
        obj.flush();
        Assert.assertNotNull("Expected 'PatientLog' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        PatientLog obj = dod.getRandomPatientLog();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PatientLog' failed to provide an identifier", id);
        obj = PatientLog.findPatientLog(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'PatientLog' with identifier '" + id + "'", PatientLog.findPatientLog(id));
    }
}
