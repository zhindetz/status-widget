package dezz.status.widget;

public class Constants {

    public interface IGibConstants {

        // Общие константы
        public static String PACKAGE_NAME = "com.salat.gbinder";
        public static String INTENT_PROPERTY_REQUEST_ACTION = "com.salat.gbinder.LISTEN_PROPERTY_CHANGES";
        public static String INTENT_PROPERTY_RESULT_ACTION = "com.salat.gbinder.PROPERTY_VALUE_CHANGED";
        public static String INTENT_SENSOR_REQUEST_ACTION = "com.salat.gbinder.GET_FLOAT_SENSOR";
        public static String INTENT_SENSOR_RESULT_ACTION = "com.salat.gbinder.SENSOR_FLOAT_CHANGED";

        int VALUE_OFF = 0;
        int VALUE_ON = 1;

        // IDs
        int getCirculationId();
        int getAcMaxId();
        int getElectricDefrostId();
        int getFrontDefrostId();
        int getBehindDefrostId();
        int getSteeringWheelId();
        int getSeatHeatId();
        int getSeatCoolId();
        int getOutdoorTempId();
        int getIndoorTempId();

        // Steering wheel heat levels
        int getSteeringWheelHeat1();
        int getSteeringWheelHeat2();
        int getSteeringWheelHeat3();

        // Seat areas
        int getSeatAreaFrontLeft();
        int getSeatAreaFrontRight();
        int getSeatAreaRearLeft();
        int getSeatAreaRearRight();

        // Seat heat levels
        int getSeatHeat1();
        int getSeatHeat2();
        int getSeatHeat3();

        // Seat cool levels
        int getSeatCool1();
        int getSeatCool2();
        int getSeatCool3();

        // Circulation values
        int getCirculationInner();
        int getCirculationOutside();
        int getCirculationAuto();
    }

    public static final IGibConstants ATLAS = new IGibConstants() {

        @Override
        public int getCirculationId() {
            return 268632320;
        }

        @Override
        public int getAcMaxId() {
            return 268502016;
        }

        @Override
        public int getElectricDefrostId() {
            return 269027328;
        }

        @Override
        public int getFrontDefrostId() {
            return 268698112;
        }

        @Override
        public int getBehindDefrostId() {
            return 268698368;
        }

        @Override
        public int getSteeringWheelId() {
            return 269025536;
        }

        @Override
        public int getSeatHeatId() {
            return 268764416;
        }

        @Override
        public int getSeatCoolId() {
            return 268763392;
        }

        @Override
        public int getOutdoorTempId() {
            return 1051392;
        }

        @Override
        public int getIndoorTempId() {
            return 1051648;
        }

        @Override
        public int getSteeringWheelHeat1() {
            return 269025537;
        }

        @Override
        public int getSteeringWheelHeat2() {
            return 269025538;
        }

        @Override
        public int getSteeringWheelHeat3() {
            return 269025539;
        }

        @Override
        public int getSeatAreaFrontLeft() {
            return 1;
        }

        @Override
        public int getSeatAreaFrontRight() {
            return 4;
        }

        @Override
        public int getSeatAreaRearLeft() {
            return 16;
        }

        @Override
        public int getSeatAreaRearRight() {
            return 64;
        }

        @Override
        public int getSeatHeat1() {
            return 268763649;
        }

        @Override
        public int getSeatHeat2() {
            return 268763650;
        }

        @Override
        public int getSeatHeat3() {
            return 268763651;
        }

        @Override
        public int getSeatCool1() {
            return 268763393;
        }

        @Override
        public int getSeatCool2() {
            return 268763394;
        }

        @Override
        public int getSeatCool3() {
            return 268763395;
        }

        @Override
        public int getCirculationInner() {
            return 268632321;
        }

        @Override
        public int getCirculationOutside() {
            return 268632322;
        }

        @Override
        public int getCirculationAuto() {
            return 268632323;
        }
    };


}
