package nncq.editplugin.tool;


public class AnnotationBuilder {


    public static String builderStart(String select, String serviceName) {
        boolean zhushi=false;
        String builderCode = "";
        String[] strings = select.split("\n");
        for (String string : strings) {
            //跳过注释
            if (builderCode.indexOf("/*")!=-1&&!zhushi&&builderCode.indexOf("*/")==-1){
                zhushi=true;
            } else if (builderCode.indexOf("*/")!=-1&&zhushi) {
                zhushi=false;
            }
            if (zhushi){
                builderCode+=string;
            }else {
                builderCode += builderCode(string, serviceName);
            }

        }
        if (builderCode.equals("")) {
            return select;
        }
        return builderCode;
    }

    public static String builderCode(String input, String serviceName) {
        int start = 0;
        String re = input+"\n";
        for (; start < input.length(); start++) {
            if (input.charAt(start) != ' ') {
                input = input.substring(start, input.length());
                break;
            }
        }
        String[] values = input.split(" ");
        if (values.length > 1) {
            switch (values[0].toUpperCase()) {
                case "R":
                    re = builderRequestMapping(input.substring(input.indexOf(values[1]),input.length()), serviceName);
                    break;
                case "REQ":
                    re = builderRequestMapping(input.substring(input.indexOf(values[1]),input.length()), serviceName);
                    break;
                case "REQUESTMAPPING":
                    re = builderRequestMapping(input.substring(input.indexOf(values[1]),input.length()), serviceName);
                    break;
                case "A":
                    re = builderApiOperation(input.substring(input.indexOf(values[1]),input.length()));
                    break;
                case "API":
                    re = builderApiOperation(input.substring(input.indexOf(values[1]),input.length()));
                    break;
                case "APIOPERATION":
                    re = builderApiOperation(input.substring(input.indexOf(values[1]),input.length()));
                    break;
            }
        }
        return re;
    }

    /**
     * 构建RequestMapping注解
     *
     * @param value 值
     * @return
     */
    public static String builderRequestMapping(String value, String serviceName) {
        String re = "";
        int start = 0, end = value.length() - 1;
        for (; start < value.length(); start++) {
            if (value.charAt(start) != ' ') {
                break;
            }
        }
        for (; end > start; end--) {
            if (value.charAt(end) != ' ') {
                break;
            }
        }
        value = value.substring(start, ++end);
        String[] values = value.split(" ");
        if (values.length > 1) {
            for (start = 0; start < values[1].length(); start++) {
                if (values[1].charAt(start) != ' ') {
                    values[1] = values[1].substring(start, values[1].length());
                    break;
                }
            }
            values[1]=values[1].toUpperCase();
            switch (values[1]) {
                case "G":
                    values[1] = "GET";
                    break;
                case "P":
                    values[1] = "POST";
                    break;
                case "D":
                    values[1] = "DELETE";
                    break;
                case "PU":
                    values[1] = "PUT";
                    break;
            }
            re = "@RequestMapping(value = \"" + values[0] + "\", method = RequestMethod." + values[1] + ")\n";
            if (!values[values.length - 1].equals("nopre")) {
                if (values[0].indexOf("/{") != -1) {
                    values[0] = values[0].substring(0, values[0].indexOf("/{"));
                }
                values[0] = values[0].replaceAll("/", "_");
                re += "@PreAuthorize(\"hasAuthority('" + serviceName + "_" + values[0] + "_" + values[1] + "')\")\n";
            }
        } else {
            re = "@RequestMapping(value = \"" + values[0] + "\", method = RequestMethod.GET)\n";
        }
        return re;
    }

    /**
     * 构建ApiOperation注解
     *
     * @param value 值
     * @return
     */
    public static String builderApiOperation(String value) {
        int start = 0;
        for (; start < value.length(); start++) {
            if (value.charAt(start) != ' ') {
                value = value.substring(start, value.length());
                break;
            }
        }
        String[] values = value.split(" ");
        if (values.length > 1) {
            for (start = 0; start < values[1].length(); start++) {
                if (values[1].charAt(start) != ' ') {
                    values[1] = values[1].substring(start, values[1].length());
                    break;
                }
            }
            return "@ApiOperation(value = \"" + values[0] + "\", notes = \"" + values[1] + "\")\n";
        } else {
            return "@ApiOperation(value = \"" + values[0] + "\", notes = \"" + values[0] + "\")\n";
        }
    }

}
