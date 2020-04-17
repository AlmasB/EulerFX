package eulerfx.core.util

import eulerfx.core.euler.Description
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object Examples {

    val list = ArrayList<Pair<String, Description> >()

    init {
        add("Venn-3", "a b c abc ab ac bc")
        add("Venn-4", "a b c d ab ac ad bc bd cd abc abd acd bcd abcd")
        add("Venn-5", "a b c d e ab ac ad ae bc bd be cd ce de abc abd abe acd ace ade bcd bce bde cde abcd abce abde acde bcde abcde")
        add("Venn-5P", "P Q R S T PQ PR PS PT QR QS QT RS RT ST PQR PQS PQT PRS PRT PST QRS QRT QST RST PQRS PQRT PQST PRST QRST PQRST")
        add("Venn-6", "a b c d e f ab ac ad ae bc bd be cd ce de abc abd abe acd ace ade bcd bce bde cde abcd abce abde acde bcde abcde af bf cf df ef abf acf adf aef bcf bdf bef cdf cef def abcf abdf abef acdf acef adef bcdf bcef bdef cdef abcdf abcef abdef acdef bcdef abcdef")

        add("Venn-4 in Venn-3", "a b c ab ac bc abc abcd abce abcf abcg abcde abcdf abcdg abcef abceg abcfg abcdef abcdeg abcdfg abcefg abcdefg")
        add("2 Venn-4 in Venn-3", "a b c ab ac bc cs ct cx cy abc cst csx csy ctx cty cxy abcd abce abcf abcg cstx csty csxy ctxy abcde abcdf abcdg abcef abceg abcfg cstxy abcdef abcdeg abcdfg abcefg abcdefg")

        add("Nested Piercing 1", "ab ac ad ae abc ade")
        add("Nested Piercing 2", "a b c ab ac bc cd ce cf cg ch cj abc")

        add("Single Piercing 1", "a b c ab ac af ag bc be cd abc abe abf abg acd acf afg bcd bce abcd abce abcf abfg ah abh")
        add("Single Piercing 2", "a b c ab ac af bc be cg ch abc abe abf abj bcd bch cgh abcd abcj")
        add("Single Piercing 3", "a b c ab ac bc bd be bf bx cg ch ci abc bcd bce bcf bcg bch bci bfx")

        add("Double Piercing", "a b c ab ac af ag bc be cd abc abe abf abg acd acf afg bcd bce abcd abce abcf abfg")
        add("Double Piercing 1", "a b c d ab ac ad ae bc bd cd abc abd acd ace bcd abcd acde")
        add("Double Piercing 2", "p q r pq pr qr qs rs pqs prs qrs qrt pqrs")
        add("Double Piercing 3", "a b c d ac ad bc bd cd ce df abd acd ace bcd bce bdf cdf abcd abce bcdf")
        add("Double Piercing 4", "a b d e ac ad ae bc bd cd de")

        add("Combined Piercing 1", "a b c ab ac af bc be cg ch co abc abe abf abj aco bcd bch bco cgh abcd abcj abco")
        add("Combined Piercing 2", "a b c d e f k ab ac ak bc bd bu ce ef abc abu bcu abcg abcl abcu abcgl")
        add("Combined Piercing 3", "a b c d e f g h j k ab ac ai aj bc bd bk ce ck df fg gh kl km kn abc abi aci bck ckl kmn abci")
        add("Combined Piercing 4", "a b c f g k l m n ab ac cd ce cf fg fh fi fj gk gl gm gn mn fhi fhj fij gmn fhij")

        add("Combined All 1", "a b c ab ac af bc be cg ch ck co abc abe abf abj ack aco bcd bch bck bco cgh cko abcd abcj abco acko bcko")
        add("Combined All 2", "a b c ab ac ad af bc be cd cg ch abc abe abf abj acd bcd bch cgh abcd abci abcj")
        add("Combined All 3", "a b c j ab ac aj ao bc bd be bf cg ch ci abc abj aco ajo bcd bce bcf bcg bch bci")
        add("Combined All 4", "b c d f h j ab ac bc bd bf bj cf de dj fh hi abc acf bfg abcf")

        add("Edge Route", "a b c ab ac bc bd bf abc abd abf bcd bcf bdf abcd abdf bcdf")
        add("Edge Route 1", "a b c d ab ac ad bc bd be cd abc abd abe acd bcd bce bde abcd abce abde")
        add("Edge Route 2", "a b c d ab ac ad ae af bc bd cd abc abd acd ace acf adf bcd abcd acde adef")

        add("Multidiagram 1", "a b c abc ab ac bc d e de df")

        add("Component Decomposition 1", "a b ab bc bd bcd")
        add("Component Decomposition 2", "a ab abc")
        add("Component Decomposition 3", "a b c ab ac bc cd ce cf abc cde cdf cef abcg abch abcj cdef abcgh abcgj abcghj abcjh")
        add("Component Decomposition 4", "d ad bd cd abd acd bcd bdg bdh abcd bdgh")

        add("Disconnecting Curve 1", "a b c abc ab ac bc x y z xy yz xz xyz ad d dx")
        add("Disconnecting Curve 2", "a b ac abc")

        add("Double Piercing Speed", "a b c abc ab ac bc ae e bcf bf x y z xy yz xz xyz ad d dx bg g ch h ci i")

        add("Decomp1", "P Q R PQ PS QS PQR PQS PQRS QRS PRS")
        add("Decomp2", "P Q R PQ PR QR QS QT RS PQR PQS PQT PRS QRS QST PQRS PQST")
        add("Decomp3", "P Q R PQ PR PS QR QS PQR PQS PRS QRS")
        add("Decomp4", "P Q R PQ PR QR QS RS PQR PQS PQT PRS QST PQRS PQST")
    }

    private fun add(name: String, description: String) {
        list.add(name.to(Description.from(description)))
    }
}