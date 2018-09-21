package se.fredin.llama.processor.join;

import org.apache.camel.Exchange;
import se.fredin.llama.processor.BaseProcessor;
import se.fredin.llama.processor.ResultType;

public abstract class AbstractJoinProcessor extends BaseProcessor {

    protected Exchange main;
    protected Exchange joining;
    protected JoinType joinType;

    public AbstractJoinProcessor() {}

    public AbstractJoinProcessor(Exchange main, Exchange joining, JoinType joinType, ResultType resultType) {
        this.main = main;
        this.joining = joining;
        this.joinType = joinType;
        this.resultType = resultType;
    }

    @Override
    protected Exchange result() {
        return this.main;
    }

    public Exchange getMain() {
        return main;
    }

    public void setMain(Exchange main) {
        this.main = main;
    }

    public Exchange getJoining() {
        return joining;
    }

    public void setJoining(Exchange joining) {
        this.joining = joining;
    }

    public JoinType getJoinType() {
        return joinType;
    }

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
