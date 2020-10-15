package com.wuc.ft_audio.mediaplayer.db;

import android.database.sqlite.SQLiteDatabase;

import com.wuc.ft_audio.app.AudioHelper;
import com.wuc.ft_audio.mediaplayer.model.AudioBean;
import com.wuc.ft_audio.mediaplayer.model.DaoMaster;
import com.wuc.ft_audio.mediaplayer.model.DaoSession;
import com.wuc.ft_audio.mediaplayer.model.Favourite;
import com.wuc.ft_audio.mediaplayer.model.FavouriteDao;

/**
 * @author: wuchao
 * @date: 2019-10-10 20:08
 * @desciption: 整个组件的数据存储中心，完成收藏/播放记录等数据存储功能
 */
public class GreenDaoHelper {

    private static final String DB_NAME = "music_db";
    /**
     * 数据库帮助类，用来创建数据库，升级数据库
     */
    private static DaoMaster.DevOpenHelper mHelper;
    /**
     * 最终创建好的数据库
     */
    private static SQLiteDatabase mDb;
    /**
     * 管理数据库
     */
    private static DaoMaster mDaoMaster;
    /**
     * 管理各种实体Dao,不让业务层拿到session直接去操作数据库，统一由此类提供方法
     */
    private static DaoSession mDaoSession;

    /**
     * 设置greenDao
     */
    public static void initDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(), DB_NAME, null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 添加收藏
     */
    public static void addFavourite(AudioBean audioBean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(audioBean.id);
        favourite.setAudioBean(audioBean);
        dao.insertOrReplace(favourite);
    }

    /**
     * 移除收藏
     */
    public static void removeFavourite(AudioBean audioBean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite =
                dao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(audioBean.id)).unique();
        dao.delete(favourite);
    }

    /**
     * 查找收藏
     */
    public static Favourite selectFavourite(AudioBean audioBean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite =
                dao.queryBuilder().where(FavouriteDao.Properties.AudioId.eq(audioBean.id)).unique();
        return favourite;
    }
}
