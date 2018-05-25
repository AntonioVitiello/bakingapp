package av.udacity.bakingapp.message;

/**
 * Created by Antonio Vitiello
 */
public class StepChangeEvent {
    private String mStepData;
    private boolean mTwoPane;
    private int mCurrentStep;
    private int mStepsCount;

    private StepChangeEvent(Builder builder) {
        mStepData = builder.mStepData;
        mTwoPane = builder.mTwoPane;
        mCurrentStep = builder.mCurrentStep;
        mStepsCount = builder.mStepsCount;
    }

    public void setStepData(String stepData) {
        mStepData = stepData;
    }

    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    public void setCurrentStep(int currentStep) {
        mCurrentStep = currentStep;
    }

    public void setStepsCount(int stepsCount) {
        mStepsCount = stepsCount;
    }

    public String getStepData() {
        return mStepData;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    public int getCurrentStep() {
        return mCurrentStep;
    }

    public int getStepsCount() {
        return mStepsCount;
    }


    public static final class Builder {
        private String mStepData;
        private boolean mTwoPane = true;
        private int mCurrentStep;
        private int mStepsCount;

        public Builder() {
        }

        public Builder stepData(String val) {
            mStepData = val;
            return this;
        }

        public Builder twoPane(boolean val) {
            mTwoPane = val;
            return this;
        }

        public Builder currentStep(int val) {
            mCurrentStep = val;
            return this;
        }

        public Builder stepsCount(int val) {
            mStepsCount = val;
            return this;
        }

        public StepChangeEvent build() {
            return new StepChangeEvent(this);
        }
    }

}
