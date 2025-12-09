package tasks.controller;

import tasks.model.Task;
import tasks.repository.TaskRepository;
import tasks.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task saved = repository.save(task);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> list = repository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        Task task = repository.findById(id)
                .map(existing -> {
                    existing.setName(updatedTask.getName());
                    existing.setDueDate(updatedTask.getDueDate());
                    existing.setAssignee(updatedTask.getAssignee());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id " + id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
