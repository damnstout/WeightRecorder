package info.damnstout.wr;

public class WeightCalc {

	public static class Range {
		private double upper;
		private double floor;

		public Range(double upper, double floor) {
			super();
			this.upper = upper;
			this.floor = floor;
		}

		public double getUpper() {
			return upper;
		}

		public void setUpper(double upper) {
			this.upper = upper;
		}

		public double getFloor() {
			return floor;
		}

		public void setFloor(double floor) {
			this.floor = floor;
		}
	}

	// Deurenberg公式1是使用比较广泛的一种脂肪率估算公式，具体为
	// 脂肪率 = (1.20×BMI) + (0.23×Age) - (10.8×gender) - 5.4
	// 公式中的BMI就是我们说的身体质量指数，你可以用 体重（kg）/身高2（m2） 计算得到。
	// Age指的是实际年龄，即周岁年龄。gender为男性时，gender=1，而为女性时，gender=0。
	public static double Bmi(double weight, double height) {
		return weight / (height * height);
	}

	public static double FatRatio(double Bmi, int age, int gender) {
		return (1.2 * Bmi) + (0.23 * age) - (10.8 * gender) - 5.4;
	}
	
	public static Range BmiRange(int age, int gender) {
		return new Range(18.5, 24);
	}

	public static Range FatRatioRange(int age, int gender) {
		switch (gender) {
		case 0:
			if (age < 18) {
				return new Range(0.1, 0.4);
			} else if (age < 40) {
				return new Range(0.21, 0.34);
			} else if (age < 60) {
				return new Range(0.22, 0.35);
			} else {
				return new Range(0.23, 0.36);
			}
		case 1:
			if (age < 18) {
				return new Range(0.05, 0.4);
			} else if (age < 40) {
				return new Range(0.11, 0.21);
			} else if (age < 60) {
				return new Range(0.12, 0.22);
			} else {
				return new Range(0.14, 0.24);
			}
		default:
			return new Range(0, 1);
		}
	}
}
