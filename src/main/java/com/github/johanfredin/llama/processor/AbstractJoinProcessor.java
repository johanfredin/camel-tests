package com.github.johanfredin.llama.processor;

import org.apache.camel.Exchange;
import com.github.johanfredin.llama.pojo.JoinType;
import com.github.johanfredin.llama.processor.generic.GenericProcessor;

/**
 * Super class for all {@link com.github.johanfredin.llama.processor.LlamaProcessor} instances where
 * there are 2 exchanges that we need to combine/compare with another.
 * @author johan
 */
public abstract class AbstractJoinProcessor extends GenericProcessor {

    protected Exchange main;
    protected Exchange joining;
    protected JoinType joinType;

    /**
     * Create a new empty instance
     */
    public AbstractJoinProcessor() {
        super(false);
    }

    /**
     * Create a new instance
     * @param main the main exchange from the route that called this processor
     * @param joining the joining exchange from another route
     * @param joinType how to filterValidateAgainst the two exchanges
     */
    public AbstractJoinProcessor(Exchange main, Exchange joining, JoinType joinType) {
        super(false);
        this.main = main;
        this.joining = joining;
        this.joinType = joinType;
    }

    /**
     * @return the main exchange from the route that called this processor
     */
    public Exchange getMain() {
        return main;
    }

    /**
     * @param main the main exchange from the route that called this processor
     */
    public void setMain(Exchange main) {
        this.main = main;
    }

    /**
     * @return the joining exchange from another route
     */
    public Exchange getJoining() {
        return joining;
    }

    /**
     * @param joining the joining exchange from another route
     */
    public void setJoining(Exchange joining) {
        this.joining = joining;
    }

    /**
     * @return how to filterValidateAgainst the two exchanges
     */
    public JoinType getJoinType() {
        return joinType;
    }

    /**
     * @param joinType how to filterValidateAgainst the two exchanges
     */
    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    @Override
    public Exchange getResult() {
        return this.main;
    }

    @Override
    public String toString() {
        return "AbstractJoinProcessor{" +
                "main=" + main +
                ", joining=" + joining +
                ", joinType=" + joinType +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
