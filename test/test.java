interface I1{
    void hello();
}
interface I2{
    void hello();
}
class A1{
    private void method1(){

    }
}
class A2 extends A1{
    private void method1(){

    }
}
public class test implements I1,I2{
    int a = 1;

    public void hello(){
        System.out.println("Hello");
    }
    public static void main(String[] args) {
        test t = new test();
        System.out.println(t.a);
        //int a = 6;
        // switch (a) {
        //     case 0:
        //     System.out.println("0");
        //         //break;
        //     case 1:
        //     System.out.println("1");
        //     //break;
        //     case 2:
        //     System.out.println("2");
        //     //break;
        //     default:
        //     System.out.println("d");
        //         //break;
        //         case 5:
        //         System.out.println("2");
        //         //break;
        // }
    }
}