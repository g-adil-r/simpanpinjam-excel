package com.example.proyeksp.ui;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.proyeksp.database.Rekening;
import com.example.proyeksp.repository.RekeningRepo;

import java.util.List;

public class RekeningViewModel extends AndroidViewModel {
    private final RekeningRepo mRepository;
    private LiveData<List<Rekening>> rekeningList;

    public RekeningViewModel(Application application) {
        super(application);
        mRepository = new RekeningRepo(application);
        rekeningList = mRepository.getRekeningList();
    }
    public LiveData<Boolean> getSuccess() {
        return mRepository.getSuccess();
    }

    LiveData<List<Rekening>> getAllRekening() { return rekeningList; }
    public void update(Rekening rekening) {mRepository.update(rekening);}
    public Rekening getRekeningByNoRek(String s) {return mRepository.findRekeningByNoRek(s);}
    public void exportToXls(Uri uri) { mRepository.exportToXls(uri); }
    public void importFromXlsx(Uri uri) {
        try {
            mRepository.importFromXlsx(uri);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public LiveData<Integer> getScanData() { return mRepository.getScanData(); }
    public LiveData<Long> getTotalSetoran() {return mRepository.getTotalSetoran();}
}
