package com.example.harabazar.Service;

public interface EventHandler {
    void handle();

    void handle(int position);

    void handle(String text);
}