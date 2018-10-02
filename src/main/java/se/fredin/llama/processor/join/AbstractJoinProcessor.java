package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.processor.ResultType;

/**
 * Super class for all {@link se.fredin.llama.processor.LlamaProcessor} instances where
 * there are 2 exchanges that we need to combine/compare with another.
 */
public abstract class AbstractJoinProcessor extends BaseProcessor {

    protected Exchange main;
    protected Exchange joining;
    protected JoinType joinType;

    /**
     * Create a new empty instance
     */
    public AbstractJoinProcessor() {}

    /**
     * Create a new instance
     * @param main the main exchange from the route that called this processor
     * @param joining the joining exchange from another route
     * @param joinType how to filterValidateAgainst the two exchanges
     * @param resultType what data type the result should be part of.
     */
    public AbstractJoinProcessor(Exchange main, Exchange joining, JoinType joinType, ResultType resultType) {
        this.main = main;
        this.joining = joining;
        this.joinType = joinType;
        this.resultType = resultType;
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
    public String toString() {
        return "AbstractJoinProcessor{" +
                "main=" + main +
                ", joining=" + joining +
                ", joinType=" + joinType +
                ", processedRecords=" + processedRecords +
                '}';
    }
}
