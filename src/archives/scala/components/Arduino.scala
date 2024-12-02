package components

class Arduino(address: String) {
    val addr: String = address

    def showAddr() : Unit = {
        println(addr)
    }
    def senseSlap(): Int = {
        new RandomGen().randZeroOne()
    }
    def senseLumen(): Int = {
        new RandomGen().randZeroToThousand()
    }
    def light(color: String, intensity: Int): Unit = {
        color match {
            case "red" => println(s"$color is lit with intensity $intensity, occupied")
            case "green" => println(s"$color is lit with intensity $intensity, available")
            case "yellow" => println(s"$color is lit with intensity $intensity, afk")
        }
    }
    def rotateCervoMotor() : Unit = println("rotating")
}

class RandomGen {
    def randZeroOne() : Int = scala.util.Random.between(0,2)
    def randZeroToThousand(): Int = scala.util.Random.between(0,1001)
}