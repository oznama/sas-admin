CREATE ALIAS IF NOT EXISTS F_UNACCENT AS 'String f_unaccent(String input) {

    input = input.replaceAll("[\u00e0\u00e1\u00e2\u00e3\u00e4\u00e5]","a");
    input = input.replaceAll("[\u00e8\u00e9\u00ea\u00eb]","e");
    input = input.replaceAll("[\u00ec\u00ed\u00ee\u00ef]","i");
    input = input.replaceAll("[\u00f2\u00f3\u00f4\u00f5\u00f6]","o");
    input = input.replaceAll("[\u00f9\u00fa\u00fb\u00fc]","u");

    input = input.replaceAll("[\u00c0\u00c1\u00c2\u00c3\u00c4\u00c5]","A");
    input = input.replaceAll("[\u00c8\u00c9\u00ca\u00cb]","E");
    input = input.replaceAll("[\u00cc\u00cd\u00ce\u00cf]","I");
    input = input.replaceAll("[\u00d2\u00d3\u00d4\u00d5\u00d6]","O");
    input = input.replaceAll("[\u00d9\u00da\u00db\u00dc]","U");

    return input;
}'