package com.teamness.smane.adapter;

import com.teamness.smane.Handleable;
import com.teamness.smane.process.ProcessRunner;

import java.io.IOException;
import java.util.List;

public abstract class ProcessAdapter<T> extends Handleable<T, T> {

    protected final ProcessRunner<T> process;

    public ProcessAdapter() {
        this.process = new ProcessRunner<>();
    }

    public abstract void init() throws IOException;

    protected void start(List<String> cmd) throws IOException {
        this.process.start(cmd);
    }

}
