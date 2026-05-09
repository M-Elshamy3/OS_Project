package service;

import model.SchedulerModels.Process;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ValidationService {

    public Integer parseInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    public void validateProcesses(List<Process> processes, List<String> errors) {
        Set<String> used = new HashSet<>();

        if (processes.isEmpty()) {
            errors.add("No processes provided.");
        }

        for (Process p : processes) {
            String name = p.getPid().isEmpty() ? "Blank PID" : p.getPid();

            validatePid(p, used, errors);
            validateArrival(p, name, errors);
            validateBurst(p, name, errors);
            validatePriority(p, name, errors);
        }
    }
    private void validatePid(Process p, Set<String> used, List<String> errors) {
        if (!p.getPid().isEmpty()) {
            if (used.contains(p.getPid())) {
                errors.add("Duplicate PID found: " + p.getPid());
            }
            used.add(p.getPid());
        }
    }
    private void validateArrival(Process p, String name, List<String> errors) {
        if (p.getArrival() < 0) {
            errors.add(name + ": Arrival Time cannot be negative.");
        }
    }
    private void validateBurst(Process p, String name, List<String> errors) {
        if (p.getBurst() <= 0) {
            errors.add(name + ": Burst Time must be greater than zero.");
        }
    }
    private void validatePriority(Process p, String name, List<String> errors) {
        if (p.getPriority() <= 0) {
            errors.add(name + ": Priority must be greater than zero.");
        }
    }
}