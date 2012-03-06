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

	// Deurenberg��ʽ1��ʹ�ñȽϹ㷺��һ��֬���ʹ��㹫ʽ������Ϊ
	// ֬���� = (1.20��BMI) + (0.23��Age) - (10.8��gender) - 5.4
	// ��ʽ�е�BMI��������˵����������ָ����������� ���أ�kg��/���2��m2�� ����õ���
	// Ageָ����ʵ�����䣬���������䡣genderΪ����ʱ��gender=1����ΪŮ��ʱ��gender=0��
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
