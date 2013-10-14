package com.github.neuralnetworks.samples;

import com.github.neuralnetworks.architecture.types.RBM;
import com.github.neuralnetworks.calculation.LayerCalculatorImpl;
import com.github.neuralnetworks.input.MeanInputModifier;
import com.github.neuralnetworks.input.ScalingInputModifier;
import com.github.neuralnetworks.input.mnist.MnistInputConverter;
import com.github.neuralnetworks.input.mnist.MnistInputProvider;
import com.github.neuralnetworks.input.mnist.MnistTargetConverter;
import com.github.neuralnetworks.neuronfunctions.AparapiSigmoidByRows;
import com.github.neuralnetworks.neuronfunctions.AparapiSigmoidByRows.AparapiSigmoidByColumns;
import com.github.neuralnetworks.neuronfunctions.RepeaterFunction;
import com.github.neuralnetworks.outputerror.MnistOutputError;
import com.github.neuralnetworks.testing.Sampler;
import com.github.neuralnetworks.training.ContrastiveDivergenceAparapiTrainer;
import com.github.neuralnetworks.training.MersenneTwisterRandomInitializer;
import com.github.neuralnetworks.util.Constants;
import com.github.neuralnetworks.util.Properties;

public class RBMSampler extends Sampler {

    public RBMSampler() {
	super();

	MnistInputConverter inputTrainingConverter = new MnistInputConverter();
	inputTrainingConverter.addModifier(new MeanInputModifier());
	inputTrainingConverter.addModifier(new ScalingInputModifier(255));


	MnistInputConverter inputTestingConverter = new MnistInputConverter();
	inputTestingConverter.addModifier(new MeanInputModifier());
	inputTestingConverter.addModifier(new ScalingInputModifier(255));

	MnistTargetConverter targetConverter = new MnistTargetConverter();
	MnistInputProvider training = new MnistInputProvider("train-images.idx3-ubyte", "train-labels.idx1-ubyte", 10, inputTrainingConverter, targetConverter);
	MnistInputProvider testing = new MnistInputProvider("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte", 1, inputTestingConverter, targetConverter);

	Properties rbmProperties = new Properties();
	rbmProperties.setParameter(Constants.HIDDEN_COUNT, 10);
	rbmProperties.setParameter(Constants.VISIBLE_COUNT, training.getRows() * training.getCols());
	rbmProperties.setParameter(Constants.FORWARD_INPUT_FUNCTION, new AparapiSigmoidByRows());
	rbmProperties.setParameter(Constants.BACKWARD_INPUT_FUNCTION, new AparapiSigmoidByColumns());
	rbmProperties.setParameter(Constants.ACTIVATION_FUNCTION, new RepeaterFunction());
	rbmProperties.setParameter(Constants.ADD_BIAS, true);
	RBM rbm = new RBM(rbmProperties);

	Properties trainerProperties = new Properties();
	trainerProperties.setParameter(Constants.NEURAL_NETWORK, rbm);
	trainerProperties.setParameter(Constants.TRAINING_INPUT_PROVIDER, training);
	trainerProperties.setParameter(Constants.TESTING_INPUT_PROVIDER, testing);
	trainerProperties.setParameter(Constants.TEST_LAYER_CALCULATOR, new LayerCalculatorImpl());
	trainerProperties.setParameter(Constants.MINI_BATCH_SIZE, 10);
	trainerProperties.setParameter(Constants.LEARNING_RATE, 0.001f);
	trainerProperties.setParameter(Constants.OUTPUT_ERROR, new MnistOutputError());
	trainerProperties.setParameter(Constants.RANDOM_INITIALIZER, new MersenneTwisterRandomInitializer(-0.1f, 0.2f));
	ContrastiveDivergenceAparapiTrainer trainer = new ContrastiveDivergenceAparapiTrainer(trainerProperties);
	trainingConfigurations.add(trainer);
    }
}
