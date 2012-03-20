package info.damnstout.wr;

public class WeightCalc {

	public static class Range {
		private double floor;
		private double upper;

		public Range(double floor, double upper) {
			super();
			this.floor = floor;
			this.upper = upper;
		}
		
		@Override
		public String toString() {
			return String.format("%.1f~%.1f", floor, upper);
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

	// Deurenberg��ʽ1��ʹ�ñȽϹ㷺��һ��֬���ʹ��㹫ʽ������Ϊ
	// ֬���� = (1.20��BMI) + (0.23��Age) - (10.8��gender) - 5.4
	// ��ʽ�е�BMI��������˵����������ָ����������� ���أ�kg��/���2��m2�� ����õ���
	// Ageָ����ʵ�����䣬���������䡣genderΪ����ʱ��gender=1����ΪŮ��ʱ��gender=0��
	public static double Bmi(double weight, double height) {
		return weight * 10000 / (height * height);
	}
	
	public static double BmiToWeight(double bmi, double height) {
		return bmi * height * height / 10000;
	}

	public static double FatRatio(double Bmi, int age, int gender) {
		return ((1.2 * Bmi) + (0.23 * age) - (10.8 * gender) - 5.4) / 100;
	}
	
	public static double FatRatioToBmi(double fatRatio, int age, int gender) {
		return (fatRatio * 100 + 5.4 + (10.8 * gender) - (0.23 * age) )/ 1.2;
	}
	
	public static Range StandardBmiRange(int age, int gender) {
		Range fatRatioRange = standardFatRatioRange(age, gender);
		double floor = FatRatioToBmi(fatRatioRange.getFloor(), age, gender);
		double upper = FatRatioToBmi(fatRatioRange.getUpper(), age, gender);
		return new Range(floor, upper);
	}
	
	public static Range StandardWeightRange(int age, int gender, int height) {
		Range bmiRange = StandardBmiRange(age, gender);
		double floor = BmiToWeight(bmiRange.getFloor(), height);
		double upper = BmiToWeight(bmiRange.getUpper(), height);
		return new Range(floor, upper);
	}

	public static Range standardFatRatioRange(int age, int gender) {
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
