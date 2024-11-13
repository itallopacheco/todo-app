package org.italo;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.time.LocalTime;
import java.util.List;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    MeterRegistry meterRegistry;

    @GET
    public List<TaskEntity> getAll(){
        return TaskEntity.listAll();
    }

    @POST
    @Transactional
    public TaskEntity create(TaskEntity task){
        TaskEntity.persist(task);
        return task;
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public TaskEntity update(@PathParam("id") Long id, Status status){
        TaskEntity entity = TaskEntity.findById(id);
        if (entity == null){
            throw new WebApplicationException("Task with id of " + id + " does not exist.", 404);
        }
        entity.status = status;
        entity.updatedAt = LocalTime.now();
        return entity;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id){
        TaskEntity entity = TaskEntity.findById(id);
        if (entity == null){
            throw new WebApplicationException("Task with id of " + id + " does not exist.", 404);
        }
        entity.active = !entity.active;
        entity.updatedAt = LocalTime.now();
    }

    @GET
    @Path("/metrics")
    public void taskMetrics(){
        Gauge.builder("tasks.total.count", this, TaskResource::getTaskCount)
                .description("Total number of tasks in the database")
                .register(meterRegistry);
        Gauge.builder("tasks.active.count", this, TaskResource::getActiveTaskCount)
                .description("Total number of active tasks in the database")
                .register(meterRegistry);
        Gauge.builder("tasks.completed.count", this, TaskResource::getCompletedTaskCount)
                .description("Total number of completed tasks in the database")
                .register(meterRegistry);
        Gauge.builder("tasks.inprogress.count", this, TaskResource::getInProgressTaskCount)
                .description("Total number of tasks in progress in the database")
                .register(meterRegistry);
        Gauge.builder("tasks.pending.count", this, TaskResource::getPendingTaskCount)
                .description("Total number of pending tasks in the database")
                .register(meterRegistry);
        Gauge.builder("tasks.inactive.count", this, TaskResource::getInactiveTaskCount)
                .description("Total number of inactive tasks in the database")
                .register(meterRegistry);
    }

    public Long getTaskCount(){
        return TaskEntity.count();
    }

    public Long getActiveTaskCount(){
        return TaskEntity.count("active", true);
    }

    public Long getInactiveTaskCount(){
        return TaskEntity.count("active", false);
    }

    public Long getPendingTaskCount(){
        return TaskEntity.count("status", Status.PENDING);
    }

    public Long getInProgressTaskCount(){
        return TaskEntity.count("status", Status.IN_PROGRESS);
    }

    public Long getCompletedTaskCount(){
        return TaskEntity.count("status", Status.COMPLETED);
    }

}
