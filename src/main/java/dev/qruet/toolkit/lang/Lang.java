package dev.qruet.toolkit.lang;

public class Lang {

    /**
     * Shortcut function for building a Lang Wrapper
     *
     * @param base
     * @return
     */
    private static LangWrapper W(String base) {
        return new LangWrapper(base);
    }

    public static class LangWrapper {

        private final String base;

        private LangWrapper(String base) {
            this.base = base;
        }

        @Override
        public String toString() {
            return T.C(base);
        }

        public String toString(String... str) {
            return T.C(String.format(base, (Object[]) str));
        }

    }

    public static class Cmd {

        public static LangWrapper UNKNOWN_ERROR = Lang.W("&cAn unknown error occurred! Please report this problem" +
                " to the server administrators.");

        public static LangWrapper SYNTAX_ERROR = Lang.W("&cSyntax Error! Did not recognize, &4%s, &ctry, &4%s&c?");

        public static LangWrapper UNKNOWN_COMMAND = Lang.W("&cUnknown Command! Please check your spelling or contact an administrator for help.");

    }

}


