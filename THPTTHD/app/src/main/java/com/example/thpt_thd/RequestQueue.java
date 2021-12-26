package com.example.thpt_thd;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class RequestQueue {

    private Queue<String> normalQueue;
    private Queue<String> urgentQueue;

    public RequestQueue() {
        super();
        normalQueue = new LinkedList<>();
        urgentQueue = new LinkedList<>();
    }

    public int size() {
        return normalQueue.size() + urgentQueue.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(@NonNull String room) {
        return normalQueue.contains(room) || urgentQueue.contains(room);
    }

    public void add(@NonNull String room, @NonNull boolean isUrgent) {
        if (isUrgent) {
            if (urgentQueue.contains(room)) {
                Log.d("success", "An Urgent request already exists");
            } else {
                normalQueue.remove(room);
                urgentQueue.add(room);
            }
        }
        if (!isUrgent) {
            if (!normalQueue.contains(room)) {
                normalQueue.add(room);
            }
        }
    }

    public void remove(@NonNull String room) {
        urgentQueue.remove(room);
        normalQueue.remove(room);
    }

    /**
     *
     * @return Pair<String, Boolean> for <room, isUrgent>
     * @throws NoSuchElementException
     */
    public Pair<String, Boolean> remove() throws NoSuchElementException {
        try {
            if (!urgentQueue.isEmpty()) {
                return new Pair<>(urgentQueue.remove(), true);
            } else {
                return new Pair<>(normalQueue.remove(), false);
            }
        } catch (Exception e) {
            Log.d("Sa Exception", "Try to remove from empty queue");
            throw new NoSuchElementException();
        }
    }

    /**
     *
     * @return Pair<String, Boolean> for <room, isUrgent>
     * @throws NoSuchElementException
     */
    public Pair<String, Boolean> peek() throws NoSuchElementException {
        try {
            if (!urgentQueue.isEmpty()) {
                return new Pair<>(urgentQueue.peek(), true);
            } else {
                return new Pair<>(normalQueue.peek(), false);
            }
        } catch (Exception e) {
            Log.d("Sa Exception", "Try to peek on empty queue");
            throw new NoSuchElementException();
        }
    }


}
