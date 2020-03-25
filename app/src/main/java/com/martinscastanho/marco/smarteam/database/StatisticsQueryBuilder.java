package com.martinscastanho.marco.smarteam.database;


class StatisticsQueryBuilder {
    static String build(DataBase.StatisticName statisticName, boolean isLimitedToOne) {
        return buildSelect(statisticName)+
                buildFrom()+
                buildWhere()+
                buildLimit(isLimitedToOne);
    }

    private static String buildSelect(DataBase.StatisticName statisticName){
        String[] primaryStatistic = getPrimaryStatistic(statisticName);
        String[] secondaryStatistic = getSecondaryStatistic(statisticName);

        assert primaryStatistic != null;
        assert secondaryStatistic != null;

        String select = "SELECT ";
        select += "'" + statisticName.toString() + "'" + " AS " + DataBase.Statistic.STATISTIC_NAME + ", ";
        select += DataBase.Player.COLUMN_NAME + ", ";
        select += primaryStatistic[0] + " AS " + DataBase.Statistic.STATISTIC_VALUE + ", ";
        select += secondaryStatistic[0] + " AS " + DataBase.Statistic.SECONDARY_STAT_VALUE + ", ";
        select += buildRank(primaryStatistic, secondaryStatistic) + "AS " + DataBase.Statistic.ROW_NUM + " ";
        return select;
    }

    private static String buildRank(String[] primaryStatistic, String[] secondaryStatistic){
        String rank = "RANK () OVER (ORDER BY ";
        rank += primaryStatistic[0] + " " + primaryStatistic[1] + ", ";
        rank += secondaryStatistic[0] + " " + secondaryStatistic[1] + ") ";

        return rank;
    }

    private static String buildFrom(){
        return "FROM " + DataBase.Player.TABLE + " ";
    }

    private static String buildWhere(){
        return "WHERE " + DataBase.Player.COLUMN_TEAM_ID + " = ? ";
    }

    private static String buildLimit(boolean isLimitedToOne){
        return isLimitedToOne ? "LIMIT 1 " : " ";
    }

    private static String[] getPrimaryStatistic(DataBase.StatisticName statisticName) {
        switch (statisticName){
            case MostMatches:
                return new String[]{DataBase.Player.COLUMN_MATCHES, "DESC"};
            case MostWins:
                return new String[]{DataBase.Player.COLUMN_WINS, "DESC"};
            case MostDraws:
                return new String[]{DataBase.Player.COLUMN_DRAWS, "DESC"};
            case MostDefeats:
                return new String[]{DataBase.Player.COLUMN_DEFEATS, "DESC"};
            case HighestWinPercentage:
                return new String[]{DataBase.Player.COLUMN_WIN_PERCENTAGE, "DESC"};
        }
        return null;
    }

    private static String[] getSecondaryStatistic(DataBase.StatisticName statisticName) {
        switch (statisticName){
            case MostWins:
            case MostDraws:
            case MostDefeats:
                return new String[]{DataBase.Player.COLUMN_MATCHES, "ASC"};
            case MostMatches:
                return new String[]{DataBase.Player.COLUMN_MATCHES_AFTER_DEBUT, "ASC"};
            case HighestWinPercentage:
                return new String[]{DataBase.Player.COLUMN_MATCHES, "DESC"};
        }
        return null;
    }
}
