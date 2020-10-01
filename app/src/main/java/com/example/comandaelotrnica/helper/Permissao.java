package com.example.comandaelotrnica.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.PasswordCallback;

public class Permissao {

    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT >= 23){
            List<String> listaPermissoes = new ArrayList<>();

            /* Percorrer as permissões passadas,
               verificando uma a uma se já tem a permissão
               liberada
            */
            for (String permissao : permissoes){
               Boolean temPermissao = ContextCompat.checkSelfPermission(activity,permissao) == PackageManager.PERMISSION_GRANTED;
               if (!temPermissao) listaPermissoes.add(permissao);
            }

            // Caso alista esteja vazia, não é necessário solicitar permição
            if (listaPermissoes.isEmpty())return true;
            String[] novasPermicoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermicoes);

            // Solicitar permissão

            ActivityCompat.requestPermissions(activity,novasPermicoes,requestCode);

        }
        return true;
    }

}
